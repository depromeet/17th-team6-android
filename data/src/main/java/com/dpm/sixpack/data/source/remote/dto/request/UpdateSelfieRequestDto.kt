package com.dpm.sixpack.data.source.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO SB Content nullable로 바꾸기
@Serializable
data class UpdateSelfieRequestDto(
    @SerialName("content")
    val content: String,
    @SerialName("deleteSelfieImage")
    val deleteSelfieImage: Boolean? = null,
)
