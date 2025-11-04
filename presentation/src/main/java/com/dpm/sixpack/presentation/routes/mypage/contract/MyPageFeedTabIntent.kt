package com.dpm.sixpack.presentation.routes.mypage.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface MyPageFeedTabIntent : UiIntent {
    data object OnPreviousMonthClick : MyPageFeedTabIntent

    data object OnNextMonthClick : MyPageFeedTabIntent

    data class OnPostClick(
        val postId: Long,
    ) : MyPageFeedTabIntent
}
