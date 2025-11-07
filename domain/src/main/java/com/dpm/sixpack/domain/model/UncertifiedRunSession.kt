package com.dpm.sixpack.domain.model

// TODO SB 승규형이 만든걸로 교체
data class UncertifiedRunSession(
    val runSessionId: Long,
    val createdAt: String,
    val updatedAt: String,
    val finishedAt: String,
    val distanceTotal: Int,
    val durationTotal: Long,
    val paceAvg: Int,
    val cadenceAvg: Int,
    val isSelfied: Boolean,
    val mapImage: String,
)
