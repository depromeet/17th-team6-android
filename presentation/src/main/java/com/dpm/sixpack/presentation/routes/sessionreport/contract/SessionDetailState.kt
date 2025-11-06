package com.dpm.sixpack.presentation.routes.sessionreport.contract

import com.dpm.sixpack.domain.model.SessionDetail
import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface SessionDetailState : UiState {
    @Parcelize
    data object Loading : SessionDetailState

    @Parcelize
    data class Success(
        val sessionDetail: SessionDetail,
    ) : SessionDetailState

    @Parcelize
    data object Error : SessionDetailState
}
