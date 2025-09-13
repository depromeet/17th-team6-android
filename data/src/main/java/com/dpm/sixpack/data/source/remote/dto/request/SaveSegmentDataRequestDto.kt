package com.dpm.sixpack.data.source.remote.dto.request

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SaveSegmentDataRequestsDto(
    @SerialName("segments")
    val segment: List<LocationDataRequestDto>,
    @SerialName("isStopped")
    val isStopped: Boolean
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class LocationDataRequestDto(
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
    val cadence: Long,
    @SerialName("distance")
    val distance: Int,
    @SerialName("time")
    val time: String
)

