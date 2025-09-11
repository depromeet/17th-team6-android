package com.dpm.sixpack.domain.running

object RunningActions {
    private const val PREFIX = "com.dpm.sixpack.runningservice.action"

    const val START_OR_RESUME = "$PREFIX.START_OR_RESUME_SERVICE"
    const val PAUSE = "$PREFIX.PAUSE_SERVICE"
    const val STOP = "$PREFIX.STOP_SERVICE"
}
