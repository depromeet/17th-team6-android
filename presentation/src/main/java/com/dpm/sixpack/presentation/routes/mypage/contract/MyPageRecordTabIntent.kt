package com.dpm.sixpack.presentation.routes.mypage.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface MyPageRecordTabIntent : UiIntent {
    data object OnPreviousMonthClick : MyPageRecordTabIntent

    data object OnNextMonthClick : MyPageRecordTabIntent

    data class OnRecordClick(
        val recordId: Long,
    ) : MyPageRecordTabIntent
}
