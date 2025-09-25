package com.dpm.sixpack.presentation.routes.home.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface HomeSideEffect : SideEffect {
    data class NavigateToSessionList(
        val goalId: Long,
    ) : HomeSideEffect

    data class NavigateToSession(
        val sessionId: Long,
    ) : HomeSideEffect

    data object NavigateToGoalEdit : HomeSideEffect
}
