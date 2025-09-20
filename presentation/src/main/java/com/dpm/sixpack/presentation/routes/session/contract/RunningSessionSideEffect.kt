package com.dpm.sixpack.presentation.routes.session.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface RunningSessionSideEffect : SideEffect {
    data class ChangeTab(
        val tab: Int,
    ) : RunningSessionSideEffect

    // 리포트가 유효하지 않을 때 종료하면 홈화면으로 돌아간다.
    data object NavigateBackToHome : RunningSessionSideEffect

    // 리포트가 유효할 때 종료하면 화면으로 이동.
    data class NavigateToReport(
        val sessionId: Long,
    ) : RunningSessionSideEffect
}
