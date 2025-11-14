package com.dpm.sixpack.presentation.routes.report.contract

import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.common.base.SideEffect
import com.dpm.sixpack.presentation.common.model.RunningSummary

sealed interface ReportSideEffect : SideEffect {
    data object NavigateBack : ReportSideEffect

    /** 기록 인증 화면으로 이동 */
    data class NavigateToPostUpload(
        val sessionId: Long,
        val mapImageUrl: String,
        val runningSummary: RunningSummary,
    ) : ReportSideEffect

    // 인증게시물(피드) 화면으로 이동
    data class NavigateToPostDetail(
        val feedId: Long,
    ) : ReportSideEffect

    data class ShowSnackBar(
        @StringRes val resId: Int,
    ) : ReportSideEffect
}
