package com.dpm.sixpack.presentation.routes.session_list.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface SessionListSideEffect : SideEffect {
    data object NavigateBack : SessionListSideEffect

    data class NavigateToGoalEdit(val goalId: Long) : SessionListSideEffect

    data class NavigateToSession(val sessionId: Long) : SessionListSideEffect

    data object ShowPreviousSessionFirstErrorMessage : SessionListSideEffect
}
