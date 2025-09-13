package com.dpm.sixpack.data.source.remote.dto.request

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class FinishRunningRequestDto(
    @SerialName("totalDistance")
    val totalDistance: Double,
    @SerialName("totalDuration")
    val totalDuration: Int,
    @SerialName("avgPace")
    val avgPace: Int,
    @SerialName("avgCadence")
    val avgCadence: Int,
    @SerialName("maxCadence")
    val maxCadence: Int
)
