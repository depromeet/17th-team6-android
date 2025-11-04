package com.dpm.sixpack.presentation.routes.friend.contract

import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.common.model.FriendUiItem
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface FriendUiState : UiState {
    @Parcelize
    data class FriendList(
        val friendList: List<FriendUiItem>,
        val showDeleteDialog: Boolean = false,
    ) : FriendUiState

    @Parcelize
    data object Loading : FriendUiState
}
