package com.dpm.sixpack.presentation.routes.freind

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.freind.contract.FriendIntent
import com.dpm.sixpack.presentation.routes.freind.contract.FriendSideEffect
import com.dpm.sixpack.presentation.routes.freind.contract.FriendUiState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<FriendUiState, FriendIntent, FriendSideEffect>() {
    override val initialState: FriendUiState = FriendUiState()

    override val container: Container<FriendUiState, FriendSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: FriendIntent) {
        TODO("Not yet implemented")
    }
}
