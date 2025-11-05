package com.dpm.sixpack.presentation.routes.postdetail.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface PostDetailSideEffect : SideEffect {
    data object NavigateToBack : PostDetailSideEffect

    data class NavigateToUserPage(
        val userId: Long,
    ) : PostDetailSideEffect

    data class NavigateToPostEdit(
        val feedId: Long,
    ) : PostDetailSideEffect

    data object NavigateToMyPage : PostDetailSideEffect

    data class ShowToast(
        val message: String,
    ) : PostDetailSideEffect
}
