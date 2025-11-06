package com.dpm.sixpack.presentation.routes.friend.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

interface FriendIntent : UiIntent

sealed interface FriendListIntent : FriendIntent {
    data class OptionClick(
        val userId: Long,
    ) : FriendListIntent

    data object AddFriendClick : FriendListIntent

    data object MyCodeCopyClick : FriendListIntent

    data object NavigateBackClick : FriendListIntent

    // 6. [수정] ViewModel의 Paging 'Trigger'를 위한 인텐트
    data object Refresh : FriendListIntent

    data object DismissOptionMenu : FriendListIntent

    data class ShowDeleteDialog(
        val userId: Long,
    ) : FriendListIntent

    data object DismissDeleteDialog : FriendListIntent

    data class ConfirmDeleteFriend(
        val userId: Long,
    ) : FriendListIntent
}

sealed interface AddFriendIntent : FriendIntent {
    data class InputChanged(
        val input: String,
    ) : AddFriendIntent

    data class AddFriendByCode(
        val code: String,
    ) : AddFriendIntent

    data object NavigateToFriendList : AddFriendIntent
}
