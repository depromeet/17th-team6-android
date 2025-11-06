package com.dpm.sixpack.domain.model

data class SessionReport(
    val sessionId: Long,
    val createdAt: String,
    val finishedAt: String,
    val distanceTotal: Int,
    val durationTotal: Int,
    val paceAvg: Int,
    val cadenceAvg: Int,
    val mapImage: String,
    val feed: SessionReportFeed? = null,
)

data class SessionReportFeed(
    val id: Long,
    val mapImage: String,
    val selfieImage: String? = null,
    val content: String,
    val createdAt: String,
)
