package com.dpm.sixpack.domain.model

data class RunningTrackPoint(
    val sessionId: Long,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Double,
    val avgPace: Int,
    val avgCadence: Int,
    val distanceInMeter: Int,
)
