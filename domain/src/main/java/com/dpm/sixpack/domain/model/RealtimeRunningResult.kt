package com.dpm.sixpack.domain.model

data class RealtimeRunningResult(
    val segmentId: Long, // 구간 ID
    val savedCount: Int, // 저장된 데이터 개수
)
