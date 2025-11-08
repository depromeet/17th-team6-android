package com.dpm.sixpack.presentation.routes.running.session.contract

import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.routes.running.RunningRouteSideEffect
import com.dpm.sixpack.presentation.routes.running.session.contract.state.PathState

sealed interface RunningSessionSideEffect : RunningRouteSideEffect {
    data object SessionFinish : RunningSessionSideEffect

    data class UpdateRunningPath(
        val newPathState: PathState,
    ) : RunningSessionSideEffect

    data class ShowToast(
        @StringRes val resId: Int,
    ) : RunningSessionSideEffect
}
