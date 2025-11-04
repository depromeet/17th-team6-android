package com.dpm.sixpack.presentation.routes.friend

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.friend.contract.FriendIntent
import com.dpm.sixpack.presentation.routes.friend.contract.FriendSideEffect
import com.dpm.sixpack.presentation.routes.friend.contract.FriendUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
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
