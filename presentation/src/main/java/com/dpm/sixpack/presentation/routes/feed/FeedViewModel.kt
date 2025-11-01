package com.dpm.sixpack.presentation.routes.feed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.dpm.sixpack.domain.repository.FeedListItem
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.domain.usecase.GetFeedsByDateUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.components.post.PostDropDownActionType
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.PostReaction
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.common.model.toPostResource
import com.dpm.sixpack.presentation.common.util.format.toYyyyMmDdString
import com.dpm.sixpack.presentation.routes.feed.contract.FeedIntent
import com.dpm.sixpack.presentation.routes.feed.contract.FeedSideEffect
import com.dpm.sixpack.presentation.routes.feed.contract.FeedUiState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedBottomSheetState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedDateUiState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedDialogState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.ReactionDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

private const val PREFETCH_WEEKS_THRESHOLD = 2L // 2주 이내 일시에 prefetch
private const val FETCH_MONTHS_RANGE = 1L // 한달씩 받아오기
private const val REACTION_DEBOUNCE_MS = 1000L // 마지막 Touch후 1초 뒤에 서버 연결

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getFeedsByDateUseCase: GetFeedsByDateUseCase,
    private val feedRepository: FeedRepository,
) : BaseViewModel<FeedUiState, FeedIntent, FeedSideEffect>() {

    override val initialState: FeedUiState = FeedUiState()

    override val container: Container<FeedUiState, FeedSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    // 유저가 인증 가능한지 여부 캐싱
    private var isCertifiable: Boolean = false

    private val pagingFlowCache = ConcurrentHashMap<LocalDate, Flow<PagingData<PostResource>>>()
    private val reactionDebounceJobs = ConcurrentHashMap<Long, Job>()

    private val selectedDateFlow = container.stateFlow.map { it.calendarState.selectedDate }.distinctUntilChanged()
    private val postCountsFlow = container.stateFlow.map { it.calendarState.postCounts }.distinctUntilChanged()
    private val optimisticPostsFlow = container.stateFlow.map { it.optimisticPosts }.distinctUntilChanged()

    val feedPagingData: Flow<PagingData<PostResource>> =
        container.stateFlow.map { it.calendarState.selectedDate }
            .distinctUntilChanged()
            .flatMapLatest { date ->
                val count = container.stateFlow.value.calendarState.postCounts[date] ?: 0
                if (count > 0) {
                    getPagingFlowForDate(date)
                } else {
                    flowOf(PagingData.empty())
                }
            }
            .cachedIn(viewModelScope)

    // TODO DateSelected에 포함
//    val feedDateState: StateFlow<FeedDateUiState> =
//        combine(selectedDateFlow, postCountsFlow) { selectedDate, postCounts ->
//            val today = LocalDate.now()
//            val canCertify = selectedDate.isEqual(today)
//            val postCount = postCounts[selectedDate] ?: 0
//
//            when {
//                postCount > 0 -> FeedDateUiState.PostsAvailable
//                postCount == 0 && canCertify -> FeedDateUiState.NoPostsAndCertifiable
//                else -> FeedDateUiState.NoPostsAndExpired
//            }
//        }.stateIn(
//            scope = viewModelScope,
//            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000L),
//            initialValue = FeedDateUiState.NoPostsAndCertifiable
//        )

    override fun onIntent(intent: FeedIntent) {
        when (intent) {
            // TopBar
            FeedIntent.OnTopBarGroupIconClick -> handleTopBarGroupIconClick()
            FeedIntent.OnTopBarAlarmIconClick -> handleTopBarAlarmIconClick()

            // Calendar
            is FeedIntent.OnDateSelected -> handleDateSelected(intent.date)
            is FeedIntent.OnVisibleWeeksChanged -> handleVisibleWeeksChanged(intent.startDate)

            // Certified Users
            FeedIntent.OnCertifiedUsersClick -> handleCertifiedUsersClick()

            // User Profile 클릭
            is FeedIntent.OnUserProfileClick -> handleUserProfileClick(intent.userId, intent.isMe)

            // Post Card
            is FeedIntent.OnPostMenuClick -> handlePostMenuClick(intent.feedId)
            is FeedIntent.OnPostImageClick -> handlePostImageClick(intent.post)
            is FeedIntent.OnPostReactionClick -> handlePostReactionClick(intent.post, intent.emoji, intent.isReacted)
            is FeedIntent.OnPostReactionLongClick -> handlePostReactionLongClick(
                intent.feedId,
                intent.reactions,
                intent.selectedEmoji
            )

            is FeedIntent.OnPostAddReactionClick -> handlePostAddReactionClick(intent.post)

            // DropDown Menu
            is FeedIntent.OnDropDownMenuClick -> handleDropDownMenuClick(intent.feedId, intent.action)

            // BottomSheet
            FeedIntent.OnBottomSheetDismiss -> handleBottomSheetDismiss()

            is FeedIntent.OnUserReactionSheetTabClick -> handleUserReactionSheetTabClick(intent.selectedEmoji)
            is FeedIntent.OnEmojiSheetEmojiSelected -> handleEmojiSheetEmojiSelected(intent.emoji)

            // Dialog
            FeedIntent.OnDialogDismiss -> handleDialogDismiss()
            FeedIntent.OnDialogConfirmClick -> handleDialogConfirmClick()

            // FAB
            FeedIntent.OnFloatingActionButtonClick -> handleFloatingActionButtonClick()
        }
    }

    /*
        각 날짜에 맞는 PostResource를 반환한다.
     */
    private fun getPagingFlowForDate(date: LocalDate): Flow<PagingData<PostResource>> {
        return pagingFlowCache.getOrPut(date) {
            val dateString = date.toYyyyMmDdString()
            val originalPagingFlow = getFeedsByDateUseCase(dateString)
                .map { pagingData: PagingData<FeedListItem> ->
                    pagingData.map { item ->
                        when (item) {
                            is FeedListItem.PostItem ->
                                item.toPostResource()

                            is FeedListItem.UserSummaryItem ->
                                throw IllegalStateException("Main Feed should not contain UserSummaryItem")
                        }
                    }
                }
            originalPagingFlow
                .combine(optimisticPostsFlow) { pagingData, optimisticPosts ->
                    pagingData.map { postResource ->
                        optimisticPosts[postResource.feedId] ?: postResource
                    }
                }
                .cachedIn(viewModelScope)
        }
    }

    // TopBar Intent
    private fun handleTopBarGroupIconClick() = intent {
        postSideEffect(FeedSideEffect.NavigateToFriend)
    }

    private fun handleTopBarAlarmIconClick() = intent {
        postSideEffect(FeedSideEffect.NavigateToAlarm)
    }

    private fun handleDateSelected(date: LocalDate) =
        intent {
            val feedDateState = handleFeedDateState(date)
            reduce {
                state.copy(
                    calendarState = state.calendarState.copy(selectedDate = date),
                    feedDateState = feedDateState
                )
            }
        }

    private fun handleFeedDateState(selectedDate: LocalDate): FeedDateUiState {
        val postCount = container.stateFlow.value.calendarState.postCounts[selectedDate] ?: 0
        return when {
            postCount > 0 -> FeedDateUiState.PostsAvailable
            postCount == 0 && isCertifiable -> FeedDateUiState.NoPostsAndCertifiable
            else -> FeedDateUiState.NoPostsAndExpired
        }
    }


    private fun handleVisibleWeeksChanged(startDate: LocalDate) {
        onWeekDisplayed(startDate)
    }

    private fun handleCertifiedUsersClick() = intent {
        postSideEffect(FeedSideEffect.NavigateToCertificationFriend)
    }

    private fun handleUserProfileClick(userId: Long, isMe: Boolean) = intent {
        if (isMe) {
            postSideEffect(FeedSideEffect.NavigateToMyPage)
        } else {
            postSideEffect(FeedSideEffect.NavigateToUserPage(userId))
        }
    }

    private fun handlePostMenuClick(feedId: Long) = intent {
        reduce { state.copy(selectedPostMenuId = feedId) }
    }

    private fun handlePostImageClick(post: PostResource) = intent {
        postSideEffect(FeedSideEffect.NavigateToPostDetail(post))
    }

    private fun handlePostReactionClick(post: PostResource, emoji: Emoji, isReacted: Boolean) = intent {
        val newPost = post.updateReaction(emoji, isReacted)
        reduce {
            state.copy(
                optimisticPosts = state.optimisticPosts + (post.feedId to newPost)
            )
        }

        // 2. Debounce 로직
        reactionDebounceJobs[post.feedId]?.cancel()
        reactionDebounceJobs[post.feedId] = viewModelScope.launch {
            delay(REACTION_DEBOUNCE_MS)
            try {
                feedRepository.postReaction(post.feedId, emoji.type)
            } catch (e: Exception) {
                // 4. 롤백
                reduce {
                    state.copy(
                        optimisticPosts = state.optimisticPosts + (post.feedId to post) // 원본으로
                    )
                }
                postSideEffect(FeedSideEffect.ShowToast("리액션 업데이트 실패"))
            } finally {
                reactionDebounceJobs.remove(post.feedId)
            }
        }
    }

    private fun handlePostReactionLongClick(feedId: Long, reactions: List<PostReaction>, selectedEmoji: Emoji) =
        intent {
            reduce {
                state.copy(
                    bottomSheetState = state.bottomSheetState.copy(reactionUsers = true),
                    reactionDetailsUiState = ReactionDetailsUiState.Success(
                        reactions = reactions,
                        selectedEmoji = selectedEmoji
                    )
                )
            }
        }

    private fun handlePostAddReactionClick(post: PostResource) = intent {
        reduce {
            state.copy(
                bottomSheetState = state.bottomSheetState.copy(emojiSelection = true),
                postForEmojiSelection = post
            )
        }
    }

    private fun handleDropDownMenuClick(feedId: Long, action: PostDropDownActionType) = intent {
        val newDialogState = when (action) {
            PostDropDownActionType.DELETE -> state.dialogState.copy(deleteFeedId = feedId, actionType = action)
            PostDropDownActionType.REPORT -> state.dialogState.copy(reportFeedId = feedId, actionType = action)
            else -> state.dialogState
        }

        reduce {
            state.copy(
                selectedPostMenuId = -1L, // 메뉴 닫기
                dialogState = newDialogState
            )
        }
    }

    private fun handleBottomSheetDismiss() = intent {
        reduce {
            state.copy(
                bottomSheetState = FeedBottomSheetState(reactionUsers = false, emojiSelection = false),
                postForEmojiSelection = null
            )
        }
    }

    private fun handleUserReactionSheetUserProfileClick(userId: Long, isMe: Boolean) = intent {
        if (isMe) {
            postSideEffect(FeedSideEffect.NavigateToMyPage)
        } else {
            postSideEffect(FeedSideEffect.NavigateToUserPage(userId))
        }
    }

    private fun handleUserReactionSheetTabClick(selectedEmoji: Emoji) = intent {
        val currentReactionState = state.reactionDetailsUiState
        if (currentReactionState is ReactionDetailsUiState.Success) {
            reduce {
                state.copy(
                    reactionDetailsUiState = currentReactionState.copy(selectedEmoji = selectedEmoji)
                )
            }
        }
    }

    private fun handleEmojiSheetEmojiSelected(emoji: Emoji) = intent {
        // (결정 5)
        val postToUpdate = state.postForEmojiSelection ?: return@intent

        val newPost = postToUpdate.updateReaction(emoji, isReacted = true)
        reduce {
            state.copy(
                optimisticPosts = state.optimisticPosts + (postToUpdate.feedId to newPost),
                bottomSheetState = FeedBottomSheetState(emojiSelection = false),
                postForEmojiSelection = null
            )
        }

        // 2. 서버 즉시 전송
        viewModelScope.launch {
            try {
                feedRepository.postReaction(postToUpdate.feedId, emoji.type)
            } catch (e: Exception) {
                // 3. 롤백
                reduce {
                    state.copy(
                        optimisticPosts = state.optimisticPosts + (postToUpdate.feedId to postToUpdate)
                    )
                }
                postSideEffect(FeedSideEffect.ShowToast("리액션 추가 실패"))
            }
        }
    }

    private fun handleDialogDismiss() = intent {
        reduce { state.copy(dialogState = FeedDialogState()) }
    }

    private fun handleDialogConfirmClick() = intent {
        val dialogState = state.dialogState

        reduce { state.copy(dialogState = FeedDialogState()) } // 다이얼로그 즉시 닫기

        when (dialogState.actionType) {
            PostDropDownActionType.DELETE -> {
                dialogState.deleteFeedId?.let { deletePost(it) }
            }

            PostDropDownActionType.REPORT -> {
                // TODO: 신고 로직
                postSideEffect(FeedSideEffect.ShowToast("신고 기능은 준비 중입니다."))
            }

            else -> {}
        }
    }

    private fun deletePost(feedId: Long) = intent {
        viewModelScope.launch {
            try {
//                deletePostUseCase(feedId)
                postSideEffect(FeedSideEffect.RefreshPagingList)
                postSideEffect(FeedSideEffect.ShowToast("게시물이 삭제되었습니다."))
            } catch (e: Exception) {
                postSideEffect(FeedSideEffect.ShowToast("삭제에 실패했습니다."))
            }
        }
    }

    private fun handleFloatingActionButtonClick() = intent {
        // (결정 6)
        val selectedDate = state.calendarState.selectedDate
        postSideEffect(FeedSideEffect.NavigateToPostUpload(selectedDate))
    }

    // 캘린더 PreFetch 로직
    private var fetchedRange: ClosedRange<LocalDate>? = null
    private val calendarApiLock = Mutex()

    // 캘린더 주차 check 함수
    private fun onWeekDisplayed(currentWeekStartDate: LocalDate) {
        val currentState = container.stateFlow.value
        if (currentState.calendarState.isLoading || currentWeekStartDate.isAfter(currentState.calendarState.today)) return

        val currentRange = fetchedRange

        if (currentRange != null &&
            currentWeekStartDate.isBefore(currentRange.start.plusWeeks(PREFETCH_WEEKS_THRESHOLD))
        ) {
            loadCalendarCounts(pivotDate = currentRange.start.minusDays(1))
        }
    }

    // 캘린더 count loading 함수
    private fun loadCalendarCounts(pivotDate: LocalDate) {
        viewModelScope.launch {
            calendarApiLock.withLock {
                intent {
                    val currentState = state
                    val calenderState = currentState.calendarState
                    reduce {
                        currentState.copy(
                            calendarState = calenderState.copy(
                                isLoading = true
                            )
                        )
                    }

                    val currentRangeEnd = fetchedRange?.endInclusive ?: pivotDate
                    val newStartDate = pivotDate.minusMonths(FETCH_MONTHS_RANGE)
                    val newEndDate = (fetchedRange?.start ?: pivotDate.plusDays(1)).minusDays(1)

                    if (newEndDate.isBefore(newStartDate)) {
                        reduce {
                            currentState.copy(
                                calendarState = calenderState.copy(
                                    isLoading = false
                                )
                            )
                        }
                        return@intent
                    }

                    feedRepository.getSelfieCalendar(
                        newStartDate.toYyyyMmDdString(),
                        newEndDate.toYyyyMmDdString()
                    ).onSuccess { selfieCounts ->
                        val newCountsMap = selfieCounts.counts.mapNotNull { selfieCount ->
                            try {
                                LocalDate.parse(selfieCount.date) to selfieCount.selfieCount
                            } catch (e: DateTimeParseException) {
                                null
                            }
                        }.toMap()

                        val mergedCounts = state.calendarState.postCounts + newCountsMap
                        fetchedRange = newStartDate..(fetchedRange?.endInclusive ?: currentRangeEnd)

                        reduce {
                            state.copy(

                                calendarState = calenderState.copy(
                                    postCounts = mergedCounts,
                                    isLoading = false
                                )
                            )
                        }

                    }
                }
            }

        }
    }

    private fun PostResource.updateReaction(emoji: Emoji, isReacted: Boolean): PostResource {
        val targetReaction = this.reactions.find { it.emoji == emoji }
        val newReactions = if (targetReaction == null && isReacted) {
            // 새 리액션 추가
            this.reactions + PostReaction(emoji = emoji, count = "1", isReacted = true, users = emptyList())
        } else if (targetReaction != null) {
            // 기존 리액션 수정
            this.reactions.map {
                if (it.emoji == emoji) {
                    val newCount = (it.count.toIntOrNull() ?: 0) + (if (isReacted) 1 else -1)
                    it.copy(count = newCount.toString(), isReacted = isReacted)
                } else {
                    it
                }
            }.filter { (it.count.toIntOrNull() ?: 0) > 0 }
        } else {
            this.reactions
        }

        return this.copy(reactions = newReactions)
    }
}
