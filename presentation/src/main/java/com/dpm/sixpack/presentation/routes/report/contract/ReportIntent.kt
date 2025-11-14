package com.dpm.sixpack.presentation.routes.report.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface ReportIntent : UiIntent {
    data object LoadSessionDetail : ReportIntent

    /** 뒤로가기 버튼 클릭 */
    data object NavigateBack : ReportIntent

    data object NavigateToPostUpload : ReportIntent

    data object NavigateToPostDetail : ReportIntent
}
