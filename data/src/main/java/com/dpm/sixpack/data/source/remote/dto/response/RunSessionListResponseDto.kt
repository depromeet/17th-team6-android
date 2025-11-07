package com.dpm.sixpack.data.source.remote.dto.response

import com.dpm.sixpack.domain.model.RunSession
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RunSessionListResponseDto(
    @SerialName("runSessionId") val runSessionId: Long,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
    @SerialName("finishedAt") val finishedAt: String,
    @SerialName("distanceTotal") val distanceTotal: Int,
    @SerialName("durationTotal") val durationTotal: Int,
    @SerialName("paceAvg") val paceAvg: Int,
    @SerialName("cadenceAvg") val cadenceAvg: Int,
    @SerialName("isSelfied") val isSelfied: Boolean,
    @SerialName("mapImage") val mapImage: String? = null,
) {
    fun toRunSession() =
        RunSession(
            runSessionId = runSessionId,
            createdAt = createdAt,
            updatedAt = updatedAt,
            finishedAt = finishedAt,
            distanceTotal = distanceTotal,
            durationTotal = durationTotal,
            paceAvg = paceAvg,
            cadenceAvg = cadenceAvg,
            isSelfied = isSelfied,
            mapImage = mapImage,
        )
}
