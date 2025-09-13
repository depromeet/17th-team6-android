package com.dpm.sixpack.domain.model

data class RealtimeRunningData(
    val latitude: Double, // 위도
    val longitude: Double, // 경도
    val altitude: Double, // 고도
    val speed: Float, // 속도 (m/s)
    val pace: Int, // 페이스 (예: "5'30\"")
    val cadence: Int, // 케이던스 (steps per minute)
    val totalDistanceMeter: Float, // 누적 거리 (m)
    val timestamp: Long, // currentTimeMillis
)
