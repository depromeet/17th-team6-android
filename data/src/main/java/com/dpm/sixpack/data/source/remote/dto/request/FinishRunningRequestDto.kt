package com.dpm.sixpack.data.source.remote.dto.request

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class FinishRunningRequestDto(
    @SerialName("data")
    val data: FinishRunningRequestData,
    @SerialName("mapImage")
    val mapImage: String,
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class FinishRunningRequestData(
    @SerialName("distance")
    val distance: DistanceRequestDto,
    @SerialName("duration")
    val duration: DurationRequestDto,
    @SerialName("pace")
    val pace: PaceRequestDto,
    @SerialName("cadence")
    val cadence: CadenceRequestDto,
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class DistanceRequestDto(
    @SerialName("total")
    val total: Int,
) {
    companion object {
        fun Int.toDistanceRequestDto(): DistanceRequestDto = DistanceRequestDto(this)
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class DurationRequestDto(
    @SerialName("total")
    val total: Int,
) {
    companion object {
        fun Int.toDurationRequestDto(): DurationRequestDto = DurationRequestDto(this)
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class PaceRequestDto(
    @SerialName("avg")
    val avg: Int,
    @SerialName("max")
    val max: MaxPaceRequestDto,
) {
    companion object {
        fun Int.toPaceRequestDto(): PaceRequestDto = PaceRequestDto(this, MaxPaceRequestDto(0, 0.0, 0.0))
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class MaxPaceRequestDto(
    @SerialName("value")
    val value: Int,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class CadenceRequestDto(
    @SerialName("avg")
    val avg: Int,
    @SerialName("max")
    val max: MaxCadenceRequestDto,
) {
    companion object {
        fun Int.toCadenceRequestDto(): CadenceRequestDto = CadenceRequestDto(this, MaxCadenceRequestDto(0))
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class MaxCadenceRequestDto(
    @SerialName("value")
    val value: Int,
)
