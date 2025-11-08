package com.dpm.sixpack.presentation.routes.report.contract

import com.dpm.sixpack.domain.model.SessionDetail
import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface ReportState : UiState {
    @Parcelize
    data object Loading : ReportState

    @Parcelize
    data class Success(
        val sessionDetail: SessionDetail,
    ) : ReportState

    @Parcelize
    data object Error : ReportState
}
