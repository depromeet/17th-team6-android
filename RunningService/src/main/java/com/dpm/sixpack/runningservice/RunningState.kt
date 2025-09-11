package com.dpm.sixpack.runningservice

data class RunningState(
    val distance: Double = 0.0,
    val paceInMoment: Double = 0.0,
    val paceAverage: Double = 0.0,
    val cadence: Int = 0
)
