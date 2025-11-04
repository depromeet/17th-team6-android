package com.dpm.sixpack.presentation.routes.friend.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface FriendIntent : UiIntent {
    data class OptionClick(
        val userId: Long,
    ) : FriendIntent

    data object AddFriendClick : FriendIntent

    data object MyCodeCopyClick : FriendIntent

    data object NavigateBackClick : FriendIntent
}
