package com.dpm.sixpack.data.source.remote.dto.response

import com.dpm.sixpack.data.source.remote.dto.request.SegmentDataDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionDetailResponseDto(
    @SerialName("id")
    val id: Long,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String,
    @SerialName("finishedAt")
    val finishedAt: String,
    @SerialName("distanceTotal")
    val distanceTotal: Int,
    @SerialName("durationTotal")
    val durationTotal: Int,
    @SerialName("paceAvg")
    val paceAvg: Int,
    @SerialName("paceMax")
    val paceMax: Int,
    @SerialName("paceMaxLatitude")
    val paceMaxLatitude: Double,
    @SerialName("paceMaxLongitude")
    val paceMaxLongitude: Double,
    @SerialName("cadenceAvg")
    val cadenceAvg: Int,
    @SerialName("cadenceMax")
    val cadenceMax: Int,
    @SerialName("mapImage")
    val mapImage: String,
    @SerialName("feed")
    val feed: SessionDetailFeedDto,
    @SerialName("segments")
    val segments: List<List<SegmentDataDto>>,
)

@Serializable
data class SessionDetailFeedDto(
    @SerialName("id")
    val id: Long,
    @SerialName("mapImage")
    val mapImage: String,
    @SerialName("selfieImage")
    val selfieImage: String,
    @SerialName("content")
    val content: String,
    @SerialName("createdAt")
    val createdAt: String,
)
