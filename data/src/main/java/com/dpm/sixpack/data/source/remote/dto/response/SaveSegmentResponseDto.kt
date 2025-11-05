package com.dpm.sixpack.data.source.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveSegmentResponseDto(
    @SerialName("segmentId")
    val segmentId: Long,
    @SerialName("savedCount")
    val savedCount: Int,
)
