package com.dpm.sixpack.presentation.routes.feed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.dpm.sixpack.data.paging.FeedPagingSource
import com.dpm.sixpack.domain.repository.FeedRepository
import com.dpm.sixpack.domain.usecase.GetFeedsByDateUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.routes.feed.contract.FeedIntent
import com.dpm.sixpack.presentation.routes.feed.contract.FeedSideEffect
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedBottomSheetState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getFeedsByDateUseCase: GetFeedsByDateUseCase,
    private val feedRepository: FeedRepository,
) : BaseViewModel<FeedUiState, FeedIntent, FeedSideEffect>() {

    override val initialState: FeedUiState = FeedUiState()

    override val container: Container<FeedUiState, FeedSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)


    private val selectedDate = MutableStateFlow(LocalDate.now())
    private val loadedMonths = mutableSetOf<YearMonth>()

    private val reactionIntentFlow = MutableStateFlow<Pair<Int, Emoji>?>(null)

    val feedPagingFlow = selectedDate.flatMapLatest { date ->
        Pager(PagingConfig(pageSize = 10)) {
            FeedPagingSource(getFeedsByDateUseCase, date)
        }.flow
    }.cachedIn(viewModelScope)

    init {
        observeReactionIntent()
    }

    override fun onIntent(intent: FeedIntent) {
        when (intent) {
            FeedIntent.OnTopBarGroupIconClick -> { /* TODO: Navigate to Group */ }
            FeedIntent.OnTopBarAlarmIconClick -> { /* TODO: Navigate to Alarm */ }
            is FeedIntent.OnDateSelected -> handleDateSelected(intent.date)
            is FeedIntent.OnVisibleWeeksChanged -> handleVisibleWeeksChanged(intent.startDate)
            FeedIntent.OnCertifiedUsersClick -> { /* TODO: Navigate to Certified User List */ }
            is FeedIntent.OnPostUserProfileClick -> { /* TODO: Navigate to User Profile or My Page */ }
            is FeedIntent.OnPostMenuClick -> intent { postSideEffect(FeedSideEffect.ShowMenuBalloon(intent.feedId)) }
            is FeedIntent.OnPostMapImageClick -> { /* TODO: Navigate to Post Detail */ }
            is FeedIntent.OnPostReactionClick -> handlePostReactionClick(intent.feedId, intent.emoji)
            is FeedIntent.OnPostReactionLongClick -> { /* TODO: Show ReactionUsersBottomSheet */ }
            is FeedIntent.OnPostAddReactionClick -> { /* TODO: Show EmojiSelectionBottomSheet */ }
            FeedIntent.OnBottomSheetDismiss -> hideBottomSheet()
            is FeedIntent.OnBottomSheetUserProfileClick -> { /* TODO: Navigate to User Profile from BottomSheet */ }
            is FeedIntent.OnEmojiSelected -> { /* TODO: Handle emoji selection from BottomSheet */ }
        }
    }

    private fun handleDateSelected(date: LocalDate) {
        selectedDate.value = date
        intent {
            reduce {
                state.copy(
                    calendarState = state.calendarState.copy(selectedDate = date)
                )
            }
        }
    }

    private fun handleVisibleWeeksChanged(startDate: LocalDate) {
        val yearMonth = YearMonth.from(startDate)
        if (!loadedMonths.contains(yearMonth)) {
            viewModelScope.launch {
                // TODO: Load calendar data from repository
                loadedMonths.add(yearMonth)
            }
        }
    }

    private fun handlePostReactionClick(feedId: Int, emoji: Emoji) {
        // Optimistic update (can be implemented here)
        reactionIntentFlow.value = feedId to emoji
    }
    
    private fun observeReactionIntent() {
        viewModelScope.launch {
            reactionIntentFlow.debounce(2000).collect { pair ->
                pair?.let { (feedId, emoji) ->
                    feedRepository.postReaction(feedId, emoji.type)
                    // TODO: Handle result
                }
            }
        }
    }

    private fun hideBottomSheet() {
        intent {
            reduce {
                state.copy(
                    bottomSheetState = FeedBottomSheetState.Hidden
                )
            }
        }
    }
}
