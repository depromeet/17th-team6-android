package com.dpm.sixpack.data.source.remote.dto.response

import com.dpm.sixpack.data.source.remote.dto.request.SegmentDataDto
import com.dpm.sixpack.domain.model.SessionDetail
import com.dpm.sixpack.domain.model.SessionDetailFeed
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
    val feed: SessionDetailFeedDto?,
    @SerialName("segments")
    val segments: List<List<SegmentDataDto>>,
) {
    fun toDomain() =
        SessionDetail(
            id = id,
            createdAt = createdAt,
            updatedAt = updatedAt,
            finishedAt = finishedAt,
            distanceTotal = distanceTotal,
            durationTotal = durationTotal,
            paceAvg = paceAvg,
            paceMax = paceMax,
            paceMaxLatitude = paceMaxLatitude,
            paceMaxLongitude = paceMaxLongitude,
            cadenceAvg = cadenceAvg,
            cadenceMax = cadenceMax,
            mapImage = mapImage,
            feed = feed?.toDomain(),
            segments = segments.map { it.map { segment -> segment.toDomain() } },
        )
}

@Serializable
data class SessionDetailFeedDto(
    @SerialName("id")
    val id: Long,
    @SerialName("mapImage")
    val mapImage: String,
    @SerialName("selfieImage")
    val selfieImage: String?,
    @SerialName("content")
    val content: String?,
    @SerialName("createdAt")
    val createdAt: String,
) {
    fun toDomain() =
        SessionDetailFeed(
            id = id,
            mapImage = mapImage,
            selfieImage = selfieImage,
            content = content,
            createdAt = createdAt,
        )
}
