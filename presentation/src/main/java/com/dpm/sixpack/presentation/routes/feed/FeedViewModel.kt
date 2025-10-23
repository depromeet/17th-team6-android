package com.dpm.sixpack.presentation.routes.feed

import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.feed.contract.FeedIntent
import com.dpm.sixpack.presentation.routes.feed.contract.FeedSideEffect
import com.dpm.sixpack.presentation.routes.feed.contract.FeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    override val initialState: FeedUiState,
    override val container: Container<FeedUiState, FeedSideEffect>
) : BaseViewModel<FeedUiState, FeedIntent, FeedSideEffect>(FeedUiState()) {

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
