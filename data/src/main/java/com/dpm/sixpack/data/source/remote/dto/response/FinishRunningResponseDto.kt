package com.dpm.sixpack.data.source.remote.dto.response

import com.dpm.sixpack.domain.model.RunningSessionResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FinishRunningResponseDto(
    @SerialName("id")
    val id: Long,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String,
    @SerialName("finalGoalId")
    val finalGoalId: Long,
    @SerialName("clearedAt")
    val clearedAt: String? = null,
    @SerialName("roundCount")
    val roundCount: Int,
    @SerialName("totalDistance")
    val totalDistance: Long,
    @SerialName("totalDuration")
    val totalDuration: Long,
    @SerialName("avgPace")
    val avgPace: Long,
    @SerialName("avgCadence")
    val avgCadence: Int,
    @SerialName("maxCadence")
    val maxCadence: Int,
) {
    fun toRunningSessionResult() =
        RunningSessionResult(
            totalDistanceMeter = totalDistance.toInt(),
            totalDurationSec = totalDuration,
            avgPace = avgPace.toInt(),
            avgCadence = avgCadence,
            maxCadence = maxCadence,
        )
}
