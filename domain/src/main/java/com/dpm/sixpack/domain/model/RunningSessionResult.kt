package com.dpm.sixpack.domain.model

data class RunningSessionResult(
    val totalDistanceMeter: Int, // 전체 거리 (m 단위, 5km)
    val totalDurationSec: Int, // 총 시간 (초 단위, 30분)
    val avgPace: Int, // 평균 페이스 (예: "5'30\"")
    val avgCadence: Int, // 평균 케이던스 (steps per minute)
)
