package com.dpm.sixpack.presentation.routes.running.session.contract

import com.dpm.sixpack.presentation.routes.running.RunningRouteIntent

sealed interface RunningSessionIntent : RunningRouteIntent {
    data object SessionStart : RunningSessionIntent

    data object RunningPause : RunningSessionIntent

    data object RunningResume : RunningSessionIntent

    data object RunningStop : RunningSessionIntent

    data object RunningStopCancel : RunningSessionIntent

    data object RunningStopConfirm : RunningSessionIntent
}
