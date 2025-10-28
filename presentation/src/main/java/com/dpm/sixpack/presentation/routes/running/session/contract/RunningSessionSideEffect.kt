package com.dpm.sixpack.presentation.routes.running.session.contract

import com.dpm.sixpack.presentation.routes.running.RunningRouteSideEffect
import com.dpm.sixpack.presentation.routes.running.session.contract.state.PathState

sealed interface RunningSessionSideEffect : RunningRouteSideEffect {
    data object SessionFinish : RunningSessionSideEffect

    data class UpdateRunningPath(
        val newPathState: PathState,
    ) : RunningSessionSideEffect
}
