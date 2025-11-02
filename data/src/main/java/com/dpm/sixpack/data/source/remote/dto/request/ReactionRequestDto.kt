package com.dpm.sixpack.data.source.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReactionRequestDto(
    @SerialName("feedId")
    val feedId: Long,
    @SerialName("emojiType")
    val emojiType: String
)
