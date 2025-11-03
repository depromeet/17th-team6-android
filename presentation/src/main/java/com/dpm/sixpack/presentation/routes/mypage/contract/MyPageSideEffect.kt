package com.dpm.sixpack.presentation.routes.mypage.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface MyPageSideEffect : SideEffect {
    data object NavigateToSettings : MyPageSideEffect

    data class NavigateToRecordDetail(
        val recordId: Long,
    ) : MyPageSideEffect
}
