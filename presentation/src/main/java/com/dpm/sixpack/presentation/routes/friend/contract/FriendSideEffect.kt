package com.dpm.sixpack.presentation.routes.friend.contract

import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface FriendSideEffect : SideEffect {
    data object NavigateToAddFriend : FriendSideEffect

    data object NavigateToBack : FriendSideEffect

    data object NavigateToFriendList : FriendSideEffect

    data class ShowToast(
        @StringRes val resId: Int,
        val args: String? = null,
    ) : FriendSideEffect

    data class CopyToClipboard(
        val content: String,
        @StringRes val successMessageResId: Int,
    ) : FriendSideEffect
}
