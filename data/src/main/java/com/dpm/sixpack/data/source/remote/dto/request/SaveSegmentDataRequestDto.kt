package com.dpm.sixpack.data.source.remote.dto.request

import android.annotation.SuppressLint
import com.dpm.sixpack.core.util.TimeUtil.formatMillisToIsoUtc
import com.dpm.sixpack.data.source.local.database.entity.RunningTrackPointEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SaveSegmentDataRequestsDto(
    @SerialName("segments")
    val segments: List<SegmentDataDto>,
    @SerialName("isStopped")
    val isStopped: Boolean,
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SegmentDataDto(
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("altitude")
    val altitude: Double,
    @SerialName("speed")
    val speed: Double,
    @SerialName("pace")
    val pace: Int,
    @SerialName("cadence")
    val cadence: Int,
    @SerialName("distance")
    val distance: Int,
    @SerialName("time")
    val time: String, // "2024-01-15T09:00:00Z"
)

fun RunningTrackPointEntity.toSegmentDataDto(): SegmentDataDto =
    SegmentDataDto(
        time = formatMillisToIsoUtc(timestamp),
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        distance = distanceInMeter,
        speed = speed,
        pace = avgPace,
        cadence = avgCadence,
    )
