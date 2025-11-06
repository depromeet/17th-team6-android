package com.dpm.sixpack.domain.model

data class SessionDetail(
    val id: Long,
    val createdAt: String,
    val updatedAt: String,
    val finishedAt: String,
    val distanceTotal: Int,
    val durationTotal: Int,
    val paceAvg: Int,
    val paceMax: Int,
    val paceMaxLatitude: Double,
    val paceMaxLongitude: Double,
    val cadenceAvg: Int,
    val cadenceMax: Int,
    val mapImage: String,
    val feed: SessionDetailFeed,
    val segments: List<List<Segment>>,
)

data class SessionDetailFeed(
    val id: Long,
    val mapImage: String,
    val selfieImage: String,
    val content: String,
    val createdAt: String,
)
