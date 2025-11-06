package com.dpm.sixpack.presentation.routes.sessionreport.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface SessionDetailIntent : UiIntent {
    data class LoadSessionDetail(
        val sessionId: Long,
    ) : SessionDetailIntent

    data object RetryLoad : SessionDetailIntent

    /** 뒤로가기 버튼 클릭 */
    data object NavigateBack : SessionDetailIntent

    data class NavigateToCertification(
        val sessionId: Long,
    ) : SessionDetailIntent
}
