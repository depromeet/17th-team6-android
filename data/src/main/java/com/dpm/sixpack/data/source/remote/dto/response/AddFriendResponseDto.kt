package com.dpm.sixpack.data.source.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddFriendResponseDto(
    @SerialName("nickname")
    val nickname: String,
)
