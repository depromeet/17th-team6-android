package com.dpm.sixpack.presentation.routes.report.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface ReportIntent : UiIntent {
    data class LoadSessionDetail(
        val sessionId: Long,
    ) : ReportIntent

    data object RetryLoad : ReportIntent

    /** 뒤로가기 버튼 클릭 */
    data object NavigateBack : ReportIntent

    data class NavigateToPostEdit(
        val sessionId: Long,
    ) : ReportIntent
}
