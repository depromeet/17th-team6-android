package com.dpm.sixpack.presentation.routes.running.contract

import com.dpm.sixpack.presentation.common.util.base.UiIntent

sealed interface RunningSessionIntent : UiIntent {
    //region Common

    data object SessionStart : RunningSessionIntent

    data object ToggleFollowingMode : RunningSessionIntent

    //endregion

    //region WarmUp

    data object WarmUpSkip : RunningSessionIntent

    data object WarmUpSkipConfirm : RunningSessionIntent

    data object WarmUpSkipCancel : RunningSessionIntent

    data object WarmUpFinish : RunningSessionIntent

    data object WarmUpContinue : RunningSessionIntent

    //endregion

    //region MainRunning

    data object MainRunningPause : RunningSessionIntent

    data object MainRunningResume : RunningSessionIntent

    data object MainRunningFinish : RunningSessionIntent

    data object MainRunningCancelFinish : RunningSessionIntent

    data object MainRunningConfirmFinish : RunningSessionIntent

    //endregion

    //region CoolDown

    data object CoolDownPause : RunningSessionIntent

    data object CoolDownResume : RunningSessionIntent

    data object CoolDownFinish : RunningSessionIntent

    data object CoolDownCancelFinish : RunningSessionIntent

    data object CoolDownConfirmFinish : RunningSessionIntent

    //endregion
}
