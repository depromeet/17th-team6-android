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
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.common.model.toPostResource
import com.dpm.sixpack.presentation.common.util.format.toYyyyMmDdString
import com.dpm.sixpack.presentation.routes.feed.contract.FeedIntent
import com.dpm.sixpack.presentation.routes.feed.contract.FeedSideEffect
import com.dpm.sixpack.presentation.routes.feed.contract.FeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val optimisticPostsFlow = container.stateFlow
        .map { it.optimisticPosts }
        .distinctUntilChanged()

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


    override fun onIntent(intent: FeedIntent) {
        when (intent) {
            FeedIntent.OnTopBarGroupIconClick -> { /* TODO: Navigate to Group */
            }

            FeedIntent.OnTopBarAlarmIconClick -> { /* TODO: Navigate to Alarm */
            }

            is FeedIntent.OnDateSelected -> handleDateSelected(intent.date)
            is FeedIntent.OnVisibleWeeksChanged -> handleVisibleWeeksChanged(intent.startDate)
            FeedIntent.OnCertifiedUsersClick -> { /* TODO: Navigate to Certified User List */
            }

            is FeedIntent.OnPostUserProfileClick -> { /* TODO: Navigate to User Profile or My Page */
            }

            is FeedIntent.OnPostMenuClick -> intent { postSideEffect(FeedSideEffect.ShowMenuBalloon(intent.feedId.toInt())) }
            is FeedIntent.OnPostImageClick -> { /* TODO: Navigate to Post Detail */
            }

            is FeedIntent.OnPostReactionClick -> {}
            is FeedIntent.OnPostReactionLongClick -> { /* TODO: Show ReactionUsersBottomSheet */
            }

            is FeedIntent.OnPostAddReactionClick -> { /* TODO: Show EmojiSelectionBottomSheet */
            }

            is FeedIntent.OnDropDownMenuClick -> { /* TODO: Handle DropDownMenuClick */
            }

            FeedIntent.OnBottomSheetDismiss -> {

            }

            is FeedIntent.OnBottomSheetUserProfileClick -> { /* TODO: Navigate to User Profile from BottomSheet */
            }

            is FeedIntent.OnEmojiSelected -> { /* TODO: Handle emoji selection from BottomSheet */
            }

            FeedIntent.OnDialogDismiss -> { /* TODO: Handle Dialog Dismiss */
            }

            FeedIntent.OnDialogConfirmClick -> { /* TODO: Handle Dialog Confirm */
            }

            FeedIntent.OnFloatingActionButtonClick -> { /* TODO: Navigate to Create Post */
            }
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

    private fun handleDateSelected(date: LocalDate) =
        intent {
            reduce {
                state.copy(
                    calendarState = state.calendarState.copy(selectedDate = date)
                )
            }
        }


    private fun handleVisibleWeeksChanged(startDate: LocalDate) {
//        val yearMonth = YearMonth.from(startDate)
//        if (!loadedMonths.contains(yearMonth)) {
//            viewModelScope.launch {
//                // TODO: Load calendar data from repository
//                loadedMonths.add(yearMonth)
//            }
//        }
    }

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
}
