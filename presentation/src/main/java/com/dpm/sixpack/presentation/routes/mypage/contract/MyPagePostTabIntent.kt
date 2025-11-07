package com.dpm.sixpack.presentation.routes.mypage.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface MyPagePostTabIntent : UiIntent {
    data class OnPostClick(
        val postId: Long,
    ) : MyPagePostTabIntent

    data object OnRetryClick : MyPagePostTabIntent
}
