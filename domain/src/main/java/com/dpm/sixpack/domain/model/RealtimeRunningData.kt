package com.dpm.sixpack.domain.model

data class RealtimeRunningData(
    val latitude: Double = 0.0, // 위도
    val longitude: Double = 0.0, // 경도
    val altitude: Double = 0.0, // 고도
    val speed: Float = 0f, // 속도 (m/s)
    val pace: Int = 0, // 페이스 (예: "5'30\"")
    val cadence: Int = 0, // 케이던스 (steps per minute)
    val totalDistanceMeter: Int, // 누적 거리 (m)
    val duration: Int = 0, // 누적 시간 (초)
    val timestamp: Long = 0L, // currentTimeMillis
)
