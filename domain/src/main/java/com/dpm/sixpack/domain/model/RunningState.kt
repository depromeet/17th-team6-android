package com.dpm.sixpack.domain.model

data class RunningState(
    val duration: Long = 0L,
    val distance: Double = 0.0,
    val paceInMoment: Double = 0.0,
    val paceAverage: Double = 0.0,
    val cadence: Int = 0,
)
