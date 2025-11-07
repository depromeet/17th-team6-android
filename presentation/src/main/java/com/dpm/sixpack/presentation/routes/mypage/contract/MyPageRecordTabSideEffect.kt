package com.dpm.sixpack.presentation.routes.mypage.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface MyPageRecordTabSideEffect : SideEffect {
    data class NavigateToRecordDetail(
        val recordId: Long,
    ) : MyPageRecordTabSideEffect
}
