package com.dpm.sixpack.presentation.routes.session.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface RunningSessionIntent : UiIntent {
    //region Common

    data object SessionStart : RunningSessionIntent

    data object ToggleFollowingMode : RunningSessionIntent

    //endregion

    //region MainRunning

    data object RunningPause : RunningSessionIntent

    data object RunningResume : RunningSessionIntent

    data object RunningStop : RunningSessionIntent

    data object RunningStopCancel : RunningSessionIntent

    data object RunningStopConfirm : RunningSessionIntent

    //endregion

    data object ClickBackIcon : RunningSessionIntent

    data class UpdatePermission(
        val isGranted: Boolean,
    ) : RunningSessionIntent
}
