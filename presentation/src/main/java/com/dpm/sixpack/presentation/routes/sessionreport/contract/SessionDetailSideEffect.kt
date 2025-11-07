package com.dpm.sixpack.presentation.routes.sessionreport.contract

import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface SessionDetailSideEffect : SideEffect {
    data object NavigateBack : SessionDetailSideEffect

    /** 기록 인증 화면으로 이동 */
    data class NavigateToCertification(
        val sessionId: Long, // 인증 화면으로 이동 시 세션 ID를 넘겨줌
    ) : SessionDetailSideEffect

    data class ShowToast(
        @StringRes val resId: Int,
    ) : SessionDetailSideEffect
}
