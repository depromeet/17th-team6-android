package com.dpm.sixpack.data.source.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddFriendRequestDto(
    @SerialName("code")
    val code: String,
)
