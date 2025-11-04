package com.dpm.sixpack.data.source.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartRunningResponseDto(
    @SerialName("sessionId")
    val sessionId: Long,
)
