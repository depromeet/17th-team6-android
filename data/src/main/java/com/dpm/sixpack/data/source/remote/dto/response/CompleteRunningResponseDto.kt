package com.dpm.sixpack.data.source.remote.dto.response

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class CompleteRunningResponseDto(
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
    val avgPace: Int,
    @SerialName("avgCadence")
    val avgCadence: Int,
    @SerialName("maxCadence")
    val maxCadence: Int
)
