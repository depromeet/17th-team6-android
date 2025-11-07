package com.dpm.sixpack.domain.model

data class RunSession(
    val runSessionId: Long,
    val createdAt: String,
    val updatedAt: String,
    val finishedAt: String,
    val distanceTotal: Int,
    val durationTotal: Int,
    val paceAvg: Int,
    val cadenceAvg: Int,
    val isSelfied: Boolean,
    val mapImage: String?,
)
