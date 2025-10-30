package com.dpm.sixpack.data.source.remote.dto.request

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class FinishRunningRequestDto(
    @SerialName("data")
    val data: RunningSessionResultDto,
    @SerialName("mapImage")
    val mapImage: String,
)
