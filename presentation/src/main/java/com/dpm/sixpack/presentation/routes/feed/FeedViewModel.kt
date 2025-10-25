package com.dpm.sixpack.presentation.routes.feed

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.feed.contract.FeedIntent
import com.dpm.sixpack.presentation.routes.feed.contract.FeedSideEffect
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<FeedUiState, FeedIntent, FeedSideEffect>() {
    override val initialState: FeedUiState = FeedUiState()
    override val container: Container<FeedUiState, FeedSideEffect> = container(initialState = initialState, savedStateHandle = savedStateHandle)


    override fun onIntent(intent: FeedIntent) {
        when (intent) {
            is FeedIntent.OnRefresh -> handleOnRefresh()
        }
    }

    private fun handleOnRefresh() = intent {
        // TODO: Implement refresh logic
        postSideEffect(FeedSideEffect.ShowToast("Refreshed!"))
    }
}
