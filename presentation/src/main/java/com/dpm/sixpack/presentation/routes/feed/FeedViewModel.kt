package com.dpm.sixpack.presentation.routes.feed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.dpm.sixpack.domain.event.FeedUpdateEvent
import com.dpm.sixpack.domain.repository.FeedListItem
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.domain.usecase.feed.GetFeedsByDateUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.components.post.PostDropDownActionType
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.PostReaction
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.common.model.PostingUserInfo
import com.dpm.sixpack.presentation.common.model.ReactingUserInfo
import com.dpm.sixpack.presentation.common.model.UserInfo
import com.dpm.sixpack.presentation.common.model.toPostResource
import com.dpm.sixpack.presentation.common.model.toPostingUserInfo
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
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

    // 마지막으로 처리한 이벤트의 타임스탬프 (중복 처리 방지)
    private var lastProcessedTimestamp = 0L

    private fun isCertifiable(selectedDate: LocalDate): Boolean {
        val today = LocalDate.now()
        return selectedDate == today
    }

    private val pagingFlowCache = ConcurrentHashMap<LocalDate, Flow<PagingData<PostResource>>>()
    private val reactionDebounceJobs = ConcurrentHashMap<Pair<Long, Emoji>, Job>()
    private val certifiedUsersCache = ConcurrentHashMap<String, List<PostingUserInfo>>()
    private val optimisticPostsFlow = container.stateFlow.map { it.optimisticPosts }.distinctUntilChanged()
    private val optimisticDeletedFeedIdsFlow =
        container.stateFlow
            .map {
                it.optimisticDeletedFeedIds
            }.distinctUntilChanged()

    val feedPagingData: Flow<PagingData<PostResource>> =
        container.stateFlow
            .map { it.calendarState.selectedDate }
            .distinctUntilChanged()
            .flatMapLatest { date ->
                val count = container.stateFlow.value.calendarState.postCounts[date] ?: 0
//                if (count > 0) {
                getPagingFlowForDate(date)
//                } else {
//                    flowOf(PagingData.empty())
//                }
            }.cachedIn(viewModelScope)
            .combine(optimisticPostsFlow) { pagingData, optimisticPosts ->
                pagingData.map { postResource ->
                    optimisticPosts[postResource.feedId] ?: postResource
                }
            }.combine(optimisticDeletedFeedIdsFlow) { pagingData, deletedFeedIds ->
                pagingData.filter { postResource ->
                    postResource.feedId !in deletedFeedIds
                }
            }

    init {
        loadInitialData()
        observeFeedUpdateEvents()
    }

    /**
     * Repository에서 발생하는 Feed 변경 이벤트를 구독
     * 수정/업로드 시 자동으로 Calendar, CertifiedUsers, Paging을 갱신
     */
    private fun observeFeedUpdateEvents() {
        // TODO Room 구현시 싹다 삭제
        viewModelScope.launch {
            feedRepository.feedUpdateEvents
                .collect { event ->
                    // 이미 처리한 이벤트는 무시 (화면 재진입 시 replay된 이벤트 방지)
                    if (event.timestamp <= lastProcessedTimestamp) {
                        return@collect
                    }

                    lastProcessedTimestamp = event.timestamp

                    when (event) {
                        is FeedUpdateEvent.Updated -> {
                            onIntent(FeedIntent.OnRefreshAll)
                        }

                        is FeedUpdateEvent.Uploaded -> {
                            onIntent(FeedIntent.OnRefreshAll)
                        }
                    }
                }
        }
    }

    override fun onIntent(intent: FeedIntent) {
        when (intent) {
            // TopBar
            FeedIntent.OnTopBarGroupIconClick -> handleTopBarGroupIconClick()
            FeedIntent.OnTopBarAlarmIconClick -> handleTopBarAlarmIconClick()

            // Calendar
            is FeedIntent.OnDateSelected -> handleDateSelected(intent.date)

            // Certified Users
            FeedIntent.OnCertifiedUsersClick -> handleCertifiedUsersClick()

            // User Profile 클릭
            is FeedIntent.OnUserProfileClick -> handleUserProfileClick(intent.userId, intent.isMe)

            // Post Card
            is FeedIntent.OnPostMenuClick -> handlePostMenuClick(intent.feedId)
            is FeedIntent.OnPostImageClick -> handlePostImageClick(intent.post)
            is FeedIntent.OnPostReactionClick -> handlePostReactionClick(intent.post, intent.emoji, intent.isReacted)
            is FeedIntent.OnPostReactionLongClick ->
                handlePostReactionLongClick(
                    intent.reactions,
                    intent.selectedEmoji,
                )

            is FeedIntent.OnPostAddReactionClick -> handlePostAddReactionClick(intent.post)

            // DropDown Menu
            is FeedIntent.OnDropDownMenuClick -> handleDropDownMenuClick(intent.post, intent.action)

            // BottomSheet
            FeedIntent.OnBottomSheetDismiss -> handleBottomSheetDismiss()

            is FeedIntent.OnUserReactionSheetTabClick -> handleUserReactionSheetTabClick(intent.selectedEmoji)
            is FeedIntent.OnEmojiSheetEmojiSelected -> handleEmojiSheetEmojiSelected(intent.emoji)

            // Dialog
            FeedIntent.OnDialogDismiss -> handleDialogDismiss()
            FeedIntent.OnDialogConfirmClick -> handleDialogConfirmClick()

            // FAB
            FeedIntent.OnFloatingActionButtonClick -> handleFloatingActionButtonClick()

            // Refresh
            FeedIntent.OnRefreshAll -> handleRefreshAll()

            // UI Observations (시스템 관찰 이벤트)
            is FeedIntent.Observed.VisibleWeeksChanged -> handleVisibleWeeksChanged(intent.startDate)
            FeedIntent.Observed.PagingDataEmpty -> handlePagingDataEmpty()
        }
    }

    /*
        각 날짜에 맞는 PostResource를 반환한다.
     */
    private fun getPagingFlowForDate(date: LocalDate): Flow<PagingData<PostResource>> =
        pagingFlowCache.getOrPut(date) {
            val dateString = date.toYyyyMmDdString()
            getFeedsByDateUseCase(dateString)
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
        }

    // TopBar Intent
    private fun handleTopBarGroupIconClick() =
        intent {
            postSideEffect(FeedSideEffect.NavigateToFriend)
        }

    private fun handleTopBarAlarmIconClick() =
        intent {
            postSideEffect(FeedSideEffect.NavigateToAlarm)
        }

    /**
     * 날짜 선택 시
     * 1. 선택된 날짜의 FeedDateState 결정
     * 2. 게시물이 있는 경우 인증 유저 로딩, 없으면 빈 상태 유지
     */
    private fun handleDateSelected(date: LocalDate) =
        intent {
            if (date == state.calendarState.selectedDate) return@intent

            val feedDateState = handleFeedDateState(date)
            val isCertifiableDate = isCertifiable(date)

            reduce {
                state.copy(
                    calendarState = state.calendarState.copy(selectedDate = date),
                    feedDateState = feedDateState,
                    isCertifiableDate = isCertifiableDate,
                    isMeCertified = false,
                    isCertifiedUsersLoading = false,
                )
            }

            // 게시물이 있는 경우에만 인증 유저 로딩
            if (feedDateState == FeedDateUiState.PostsAvailable) {
                loadCertifiedUsers(date.toYyyyMmDdString())
            }
        }

    private fun handleFeedDateState(selectedDate: LocalDate): FeedDateUiState {
        val postCount = container.stateFlow.value.calendarState.postCounts[selectedDate] ?: 0
        return when {
            postCount > 0 -> FeedDateUiState.PostsAvailable
            postCount == 0 && isCertifiable(selectedDate) -> FeedDateUiState.NoPostsAndCertifiable
            else -> FeedDateUiState.NoPostsAndExpired
        }
    }

    /**
     * 초기 데이터 로딩
     * 1. 오늘 날짜 기준 캘린더 데이터 로딩
     * 2. 오늘 날짜의 인증 유저 및 내 정보 로딩
     * 3. FeedDateState 결정 (PostsAvailable, NoPostsAndCertifiable, NoPostsAndExpired)
     */
    private fun loadInitialData() =
        intent {
            val today = LocalDate.now()
            val todayString = today.toYyyyMmDdString()

            loadCalendarCounts(pivotDate = today, updateFeedDateState = true)

            loadCertifiedUsers(todayString)
        }

    private fun loadCertifiedUsers(
        date: String,
        forceRefresh: Boolean = false,
    ) = intent {
        val cached = certifiedUsersCache[date]
        if (!forceRefresh && cached != null) {
            val myInfo = cached.find { it.user.isMe }
            reduce {
                state.copy(
                    postingUserInfo = cached,
                    myPostingInfo = myInfo,
                    isMeCertified = myInfo != null,
                    isCertifiedUsersLoading = false,
                )
            }
            return@intent
        }

        reduce {
            state.copy(isCertifiedUsersLoading = true)
        }

        viewModelScope.launch {
            feedRepository
                .getCertifiedUsers(date)
                .onSuccess { certifiedUsers ->
                    val postingUsers =
                        certifiedUsers
                            .map { it.toPostingUserInfo() }
                            .distinctBy { it.user.id }
                    val myInfo = postingUsers.find { it.user.isMe }

                    certifiedUsersCache[date] = postingUsers

                    reduce {
                        state.copy(
                            postingUserInfo = postingUsers,
                            myPostingInfo = myInfo,
                            isMeCertified = myInfo != null,
                            isCertifiedUsersLoading = false,
                        )
                    }
                }.onError { error ->
                    // 에러 발생 시에도 로딩 해제
                    reduce {
                        state.copy(isCertifiedUsersLoading = false)
                    }
                }
        }
    }

    private fun handleVisibleWeeksChanged(startDate: LocalDate) {
        onWeekDisplayed(startDate)
    }

    private fun handleCertifiedUsersClick() =
        intent {
            val selectedDate = state.calendarState.selectedDate
            postSideEffect(FeedSideEffect.NavigateToCertificationFriend(selectedDate.toYyyyMmDdString()))
        }

    private fun handleUserProfileClick(
        userId: Long,
        isMe: Boolean,
    ) = intent {
        Timber.d("SR-N userId $userId, $isMe")
        if (isMe) {
            postSideEffect(FeedSideEffect.NavigateToMyPage)
        } else {
            postSideEffect(FeedSideEffect.NavigateToUserPage(userId))
        }
    }

    private fun handlePostMenuClick(feedId: Long) =
        intent {
            reduce {
                state.copy(selectedPostMenuId = if (feedId == -1L) null else feedId)
            }
        }

    private fun handlePostImageClick(post: PostResource) =
        intent {
            postSideEffect(FeedSideEffect.NavigateToPostDetail(post))
        }

    private fun handlePostReactionClick(
        post: PostResource,
        emoji: Emoji,
        isReacted: Boolean,
    ) = intent {
        val reactionKey = post.feedId to emoji
        val newPost = post.updateReaction(emoji, isReacted)
        reduce {
            state.copy(
                optimisticPosts = state.optimisticPosts + (post.feedId to newPost),
            )
        }

        reactionDebounceJobs[reactionKey]?.cancel()
        reactionDebounceJobs[reactionKey] =
            viewModelScope.launch {
                delay(REACTION_DEBOUNCE_MS)
                try {
                    feedRepository.postReaction(post.feedId, emoji.type)
                } catch (e: Exception) {
                    // 롤백: optimisticPosts에서 제거하여 원본 Paging 데이터가 보이도록 함
                    reduce {
                        state.copy(
                            optimisticPosts = state.optimisticPosts - post.feedId,
                        )
                    }
                    postSideEffect(FeedSideEffect.ShowToast("리액션 업데이트 실패"))
                } finally {
                    reactionDebounceJobs.remove(reactionKey)
                }
            }
    }

    private fun handlePostReactionLongClick(
        reactions: List<PostReaction>,
        selectedEmoji: Emoji,
    ) = intent {
        reduce {
            state.copy(
                bottomSheetState = state.bottomSheetState.copy(reactionUsers = true),
                reactionDetailsUiState =
                    ReactionDetailsUiState.Success(
                        reactions = reactions,
                        selectedEmoji = selectedEmoji,
                    ),
            )
        }
    }

    private fun handlePostAddReactionClick(post: PostResource) =
        intent {
            reduce {
                state.copy(
                    bottomSheetState = state.bottomSheetState.copy(emojiSelection = true),
                    postForEmojiSelection = post,
                )
            }
        }

    private fun handleDropDownMenuClick(
        post: PostResource,
        action: PostDropDownActionType,
    ) = intent {
        when (action) {
            PostDropDownActionType.EDIT -> {
                reduce {
                    state.copy(selectedPostMenuId = -1L)
                }
                postSideEffect(FeedSideEffect.NavigateToPostEdit(post.feedId))
            }

            PostDropDownActionType.DELETE -> {
                val newDialogState = state.dialogState.copy(deleteFeedId = post.feedId, actionType = action)
                reduce {
                    state.copy(
                        selectedPostMenuId = -1L,
                        dialogState = newDialogState,
                    )
                }
            }

            PostDropDownActionType.REPORT -> {
                val newDialogState = state.dialogState.copy(reportFeedId = post.feedId, actionType = action)
                reduce {
                    state.copy(
                        selectedPostMenuId = -1L,
                        dialogState = newDialogState,
                    )
                }
            }

            PostDropDownActionType.SAVE_IMAGE -> {
                // TODO: 이미지 저장 로직
                reduce {
                    state.copy(selectedPostMenuId = -1L)
                }
                postSideEffect(FeedSideEffect.ShowToast("이미지 저장 기능은 준비 중입니다."))
            }

            PostDropDownActionType.IDLE -> {
            }
        }
    }

    private fun handleBottomSheetDismiss() =
        intent {
            reduce {
                state.copy(
                    bottomSheetState = FeedBottomSheetState(reactionUsers = false, emojiSelection = false),
                    postForEmojiSelection = null,
                )
            }
        }

    private fun handleUserReactionSheetTabClick(selectedEmoji: Emoji) =
        intent {
            val currentReactionState = state.reactionDetailsUiState
            if (currentReactionState is ReactionDetailsUiState.Success) {
                reduce {
                    state.copy(
                        reactionDetailsUiState = currentReactionState.copy(selectedEmoji = selectedEmoji),
                    )
                }
            }
        }

    private fun handleEmojiSheetEmojiSelected(emoji: Emoji) =
        intent {
            val postToUpdate = state.postForEmojiSelection ?: return@intent

            val newPost = postToUpdate.updateReaction(emoji, isReacted = true)
            reduce {
                state.copy(
                    optimisticPosts = state.optimisticPosts + (postToUpdate.feedId to newPost),
                    bottomSheetState = FeedBottomSheetState(emojiSelection = false),
                    postForEmojiSelection = null,
                )
            }

            viewModelScope.launch {
                try {
                    feedRepository.postReaction(postToUpdate.feedId, emoji.type)
                } catch (e: Exception) {
                    reduce {
                        state.copy(
                            optimisticPosts = state.optimisticPosts + (postToUpdate.feedId to postToUpdate),
                        )
                    }
                    postSideEffect(FeedSideEffect.ShowToast("리액션 추가 실패"))
                }
            }
        }

    private fun handleDialogDismiss() =
        intent {
            reduce { state.copy(dialogState = FeedDialogState()) }
        }

    private fun handleDialogConfirmClick() =
        intent {
            val dialogState = state.dialogState

            reduce { state.copy(dialogState = FeedDialogState()) }

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

    private fun deletePost(feedId: Long) =
        intent {
            reduce {
                state.copy(
                    optimisticDeletedFeedIds = state.optimisticDeletedFeedIds + feedId,
                )
            }

            viewModelScope.launch {
                feedRepository
                    .deletePost(feedId)
                    .onSuccess {
                        postSideEffect(FeedSideEffect.ShowToast("게시물이 삭제되었습니다."))
                        onIntent(FeedIntent.OnRefreshAll)
                    }.onError { exception ->
                        reduce {
                            state.copy(
                                optimisticDeletedFeedIds = state.optimisticDeletedFeedIds - feedId,
                            )
                        }
                        postSideEffect(FeedSideEffect.ShowToast("삭제에 실패했습니다."))
                    }
            }
        }

    private fun handleFloatingActionButtonClick() =
        intent {
            val selectedDate = state.calendarState.selectedDate
            postSideEffect(FeedSideEffect.NavigateToPostUpload(selectedDate))
        }

    /**
     * 전체 새로고침
     * 1. 오늘부터 인증 가능 기간까지의 Calendar postCounts 업데이트
     * 2. 현재 선택된 날짜의 certifiedUsers 업데이트
     * 3. Paging data refresh
     */
    private fun handleRefreshAll() =
        intent {
            val today = LocalDate.now()
            val certifiableStartDate = today
            val selectedDate = state.calendarState.selectedDate

            feedRepository
                .getSelfieCalendar(
                    certifiableStartDate.toYyyyMmDdString(),
                    today.toYyyyMmDdString(),
                ).onSuccess { selfieCounts ->
                    val newCountsMap =
                        selfieCounts.counts
                            .mapNotNull { selfieCount ->
                                try {
                                    LocalDate.parse(selfieCount.date) to selfieCount.selfieCount
                                } catch (e: DateTimeParseException) {
                                    null
                                }
                            }.toMap()

                    val mergedCounts = state.calendarState.postCounts + newCountsMap

                    reduce {
                        state.copy(
                            calendarState = state.calendarState.copy(postCounts = mergedCounts),
                        )
                    }

                    val feedDateState = handleFeedDateState(selectedDate)
                    reduce {
                        state.copy(feedDateState = feedDateState)
                    }
                }.onError { exception ->
                }

            loadCertifiedUsers(selectedDate.toYyyyMmDdString(), forceRefresh = true)

            postSideEffect(FeedSideEffect.RefreshPagingList)
        }

    private fun handlePagingDataEmpty() =
        intent {
            val selectedDate = state.calendarState.selectedDate
            val canCertify = state.feedDateState == FeedDateUiState.PostsAvailable && isCertifiable(selectedDate)
            reduce {
                state.copy(
                    feedDateState =
                        if (canCertify) {
                            FeedDateUiState.NoPostsAndCertifiable
                        } else {
                            FeedDateUiState.NoPostsAndExpired
                        },
                )
            }
        }

    // 캘린더 PreFetch 로직
    private var fetchedRange: ClosedRange<LocalDate>? = null
    private val calendarApiLock = Mutex()

    /**
     * 캘린더 주차 변경 감지
     * - 사용자가 스크롤하여 2주 이내의 주차에 도달하면 추가 데이터 로딩
     * - 미래 날짜는 로딩하지 않음
     */
    private fun onWeekDisplayed(currentWeekStartDate: LocalDate) {
        val currentState = container.stateFlow.value
        if (currentState.calendarState.isLoading ||
            currentWeekStartDate.isAfter(currentState.calendarState.today)
        ) {
            return
        }

        val currentRange = fetchedRange

        if (currentRange != null &&
            currentWeekStartDate.isBefore(currentRange.start.plusWeeks(PREFETCH_WEEKS_THRESHOLD))
        ) {
            loadCalendarCounts(pivotDate = currentRange.start.minusDays(1))
        }
    }

    /**
     * 캘린더 카운트 로딩 (1개월 단위)
     * - Mutex로 동시 호출 방지
     * - 기존 데이터와 병합하여 캐시
     * @param pivotDate 기준 날짜
     * @param updateFeedDateState true일 경우 로딩 완료 후 현재 선택된 날짜의 feedDateState 업데이트
     */
    private fun loadCalendarCounts(
        pivotDate: LocalDate,
        updateFeedDateState: Boolean = false,
    ) {
        viewModelScope.launch {
            calendarApiLock.withLock {
                intent {
                    val currentState = state
                    val calenderState = currentState.calendarState
                    reduce {
                        currentState.copy(
                            calendarState =
                                calenderState.copy(
                                    isLoading = true,
                                ),
                        )
                    }

                    val currentRangeEnd = fetchedRange?.endInclusive ?: pivotDate
                    val newStartDate = pivotDate.minusMonths(FETCH_MONTHS_RANGE)
                    val newEndDate = (fetchedRange?.start ?: pivotDate.plusDays(1)).minusDays(1)

                    if (newEndDate.isBefore(newStartDate)) {
                        reduce {
                            currentState.copy(
                                calendarState =
                                    calenderState.copy(
                                        isLoading = false,
                                    ),
                            )
                        }
                        return@intent
                    }

                    feedRepository
                        .getSelfieCalendar(
                            newStartDate.toYyyyMmDdString(),
                            newEndDate.toYyyyMmDdString(),
                        ).onSuccess { selfieCounts ->
                            val newCountsMap =
                                selfieCounts.counts
                                    .mapNotNull { selfieCount ->
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
                                    calendarState =
                                        calenderState.copy(
                                            postCounts = mergedCounts,
                                            isLoading = false,
                                        ),
                                )
                            }

                            // 초기 로딩 시 현재 선택된 날짜의 feedDateState 업데이트
                            if (updateFeedDateState) {
                                val selectedDate = state.calendarState.selectedDate
                                val feedDateState = handleFeedDateState(selectedDate)
                                reduce {
                                    state.copy(feedDateState = feedDateState)
                                }
                            }
                        }.onError { exception ->
                            // API 호출 실패 시 로딩 상태를 해제하여 Shimmer가 사라지도록 함
                            reduce {
                                state.copy(
                                    calendarState =
                                        calenderState.copy(
                                            isLoading = false,
                                        ),
                                )
                            }
                        }
                }
            }
        }
    }

    private fun PostResource.updateReaction(
        emoji: Emoji,
        isReacted: Boolean,
    ): PostResource {
        val targetReaction = this.reactions.find { it.emoji == emoji }

        // 캐싱된 내 정보 사용 (Single Source of Truth)
        val currentState = container.stateFlow.value
        val myUserInfo =
            currentState.myPostingInfo?.user
                ?: UserInfo(id = -1L, name = "나", profileImageUrl = "", isMe = true)

        val currentTime = System.currentTimeMillis().toString()

        val myReactingUserInfo =
            ReactingUserInfo(
                user = myUserInfo,
                reactedAt = currentTime,
                emoji = emoji,
            )

        val newReactions =
            if (targetReaction == null && isReacted) {
                // 새 리액션 추가
                this.reactions +
                    PostReaction(
                        emoji = emoji,
                        count = "1",
                        isReacted = true,
                        users = listOf(myReactingUserInfo),
                    )
            } else if (targetReaction != null) {
                // 기존 리액션 수정
                this.reactions
                    .map {
                        if (it.emoji == emoji) {
                            val newCount = (it.count.toIntOrNull() ?: 0) + (if (isReacted) 1 else -1)
                            val newUsers =
                                if (isReacted) {
                                    // 리액션 추가: 현재 사용자를 리스트에 추가
                                    it.users + myReactingUserInfo
                                } else {
                                    // 리액션 제거: 현재 사용자를 리스트에서 제거
                                    it.users.filterNot { user -> user.user.isMe }
                                }
                            it.copy(
                                count = newCount.toString(),
                                isReacted = isReacted,
                                users = newUsers,
                            )
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
