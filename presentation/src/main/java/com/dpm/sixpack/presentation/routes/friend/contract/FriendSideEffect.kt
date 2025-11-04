package com.dpm.sixpack.presentation.routes.friend.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface FriendSideEffect : SideEffect {
    data class NavigateToProfile(
        val userId: String,
    ) : FriendSideEffect

    data class ShowToast(
        val message: String,
    ) : FriendSideEffect
}
