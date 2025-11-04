package com.dpm.sixpack.presentation.routes.mypage.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface MyPageFeedTabSideEffect : SideEffect {
    data class NavigateToPostDetail(
        val postId: Long,
    ) : MyPageFeedTabSideEffect
}
