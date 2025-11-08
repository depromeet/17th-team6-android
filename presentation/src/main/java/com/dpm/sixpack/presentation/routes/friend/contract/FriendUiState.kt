package com.dpm.sixpack.presentation.routes.friend.contract

import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface FriendUiState : UiState {
    @Parcelize
    data class FriendList(
        val showOptionForUserId: Long? = null,
        val showDeleteDialogForUserId: Long? = null,
    ) : FriendUiState

    @Parcelize
    data class AddingFriend(
        val input: String = "",
        val enterButtonEnabled: Boolean = false,
    ) : FriendUiState
}
