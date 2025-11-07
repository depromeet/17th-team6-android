package com.dpm.sixpack.domain.model

data class RealtimeRunningData(
    val latitude: Double = 0.0, // 위도
    val longitude: Double = 0.0, // 경도
    val altitude: Double = 0.0, // 고도
    val speed: Double = 0.0, // 속도 (m/s)
    val avgPace: Int = 0, // 페이스 (예: "5'30\"")
    val avgCadence: Int = 0, // 케이던스 (steps per minute)
    val maxPace: MaxPaceData = MaxPaceData.default,
    val maxCadence: Int = 0,
    val distanceInMeter: Int, // 누적 거리 (m)
    val durationInSec: Int = 0, // 누적 시간 (초)
    val timestamp: Long = 0L, // currentTimeMillis
)
