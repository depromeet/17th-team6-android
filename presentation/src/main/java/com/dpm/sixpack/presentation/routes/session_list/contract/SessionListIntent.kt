package com.dpm.sixpack.presentation.routes.session_list.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface SessionListIntent : UiIntent {
    data object NavigateBackClick : SessionListIntent

    data object GoalEditClick : SessionListIntent

    data class RunningSessionClick(val sessionId: Long) : SessionListIntent

    data class StartRunningSessionClick(val sessionId: Long) : SessionListIntent
}
