package com.dpm.sixpack.presentation.routes.session.contract

import com.dpm.sixpack.presentation.util.base.UiIntent

sealed interface RunningSessionIntent : UiIntent {
    data object Start : RunningSessionIntent

    data object Pause : RunningSessionIntent

    data object Resume : RunningSessionIntent

    data object CancelFinish : RunningSessionIntent

    data object Finish : RunningSessionIntent

    data object ConfirmFinish : RunningSessionIntent
}
