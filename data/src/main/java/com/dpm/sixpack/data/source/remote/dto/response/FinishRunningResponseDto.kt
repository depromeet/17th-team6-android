package com.dpm.sixpack.data.source.remote.dto.response

import android.annotation.SuppressLint
import com.dpm.sixpack.domain.model.RunningSessionResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class FinishRunningResponseDto(
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
) {
    fun toRunningSessionResult() =
        RunningSessionResult(
            totalDistanceMeter = distanceTotal,
            totalDurationSec = durationTotal,
            avgPace = paceAvg,
            avgCadence = cadenceAvg,
        )
}
