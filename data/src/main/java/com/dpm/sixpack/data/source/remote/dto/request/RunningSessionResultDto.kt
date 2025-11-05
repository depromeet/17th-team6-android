package com.dpm.sixpack.data.source.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RunningSessionResultDto(
    @SerialName("distance")
    val distance: DistanceRequestDto,
    @SerialName("duration")
    val duration: DurationRequestDto,
    @SerialName("pace")
    val pace: PaceRequestDto,
    @SerialName("cadence")
    val cadence: CadenceRequestDto,
)

@Serializable
data class DistanceRequestDto(
    @SerialName("total")
    val total: Int,
)

@Serializable
data class DurationRequestDto(
    @SerialName("total")
    val total: Int,
)

@Serializable
data class PaceRequestDto(
    @SerialName("avg")
    val avg: Int,
    @SerialName("max")
    val max: MaxPaceRequestDto,
)

@Serializable
data class MaxPaceRequestDto(
    @SerialName("value")
    val value: Int,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
)

@Serializable
data class CadenceRequestDto(
    @SerialName("avg")
    val avg: Int,
    @SerialName("max")
    val max: MaxCadenceRequestDto,
)

@Serializable
data class MaxCadenceRequestDto(
    @SerialName("value")
    val value: Int,
)
