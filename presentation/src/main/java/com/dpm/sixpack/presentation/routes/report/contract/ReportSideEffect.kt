package com.dpm.sixpack.presentation.routes.report.contract

import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface ReportSideEffect : SideEffect {
    data object NavigateBack : ReportSideEffect

    /** 기록 인증 화면으로 이동 */
    data class NavigateToPostEdit(
        val sessionId: Long, // 인증 화면으로 이동 시 세션 ID를 넘겨줌
    ) : ReportSideEffect

    data class ShowToast(
        @StringRes val resId: Int,
    ) : ReportSideEffect
}
