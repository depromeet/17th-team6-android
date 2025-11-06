package com.dpm.sixpack.data.source.remote.dto.request

import com.dpm.sixpack.domain.model.MaxPaceData
import com.dpm.sixpack.domain.model.RunningSessionResult
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
) {
    fun toRunningSessionResult() =
        RunningSessionResult(
            totalDistanceMeter = distance.toInt(),
            totalDurationSec = duration.toInt(),
            avgPace = pace.toInt(),
            maxPace = pace.max.toMaxPaceData(),
            avgCadence = cadence.toInt(),
            maxCadence = cadence.max.value,
        )
}

fun RunningSessionResult.toDto() =
    RunningSessionResultDto(
        distance = DistanceRequestDto(totalDistanceMeter),
        duration = DurationRequestDto(totalDurationSec),
        pace =
            PaceRequestDto(
                avgPace,
                MaxPaceRequestDto(
                    maxPace.value,
                    maxPace.latitude,
                    maxPace.longitude,
                ),
            ),
        cadence = CadenceRequestDto(avgCadence, MaxCadenceRequestDto(0)),
    )

@Serializable
data class DistanceRequestDto(
    @SerialName("total")
    val total: Int,
) {
    fun toInt() = total
}

@Serializable
data class DurationRequestDto(
    @SerialName("total")
    val total: Int,
) {
    fun toInt() = total
}

@Serializable
data class PaceRequestDto(
    @SerialName("avg")
    val avg: Int,
    @SerialName("max")
    val max: MaxPaceRequestDto,
) {
    fun toInt() = avg
}

@Serializable
data class MaxPaceRequestDto(
    @SerialName("value")
    val value: Int,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
) {
    fun toMaxPaceData() = MaxPaceData(value, latitude, longitude)
}

@Serializable
data class CadenceRequestDto(
    @SerialName("avg")
    val avg: Int,
    @SerialName("max")
    val max: MaxCadenceRequestDto,
) {
    fun toInt() = avg
}

@Serializable
data class MaxCadenceRequestDto(
    @SerialName("value")
    val value: Int,
)
