package com.dpm.sixpack.presentation.routes.report.contract

import com.dpm.sixpack.domain.model.SessionDetail
import com.dpm.sixpack.domain.model.Uploadable
import com.dpm.sixpack.domain.model.UploadableReason
import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface ReportState : UiState {
    @Parcelize
    data object Loading : ReportState

    @Parcelize
    data class Success(
        val sessionDetail: SessionDetail,
        val bottomBarType: ReportBottomBarType = ReportBottomBarType.NONE,
    ) : ReportState

    @Parcelize
    data class Error(
        val code: Int? = null,
    ) : ReportState
}

enum class ReportBottomBarType {
    NONE,
    UPLOAD,
    DETAIL,
}

fun Uploadable.toBottomBarType(): ReportBottomBarType =
    if (this.isUploadable) {
        ReportBottomBarType.UPLOAD
    } else {
        ReportBottomBarType.NONE
    }
