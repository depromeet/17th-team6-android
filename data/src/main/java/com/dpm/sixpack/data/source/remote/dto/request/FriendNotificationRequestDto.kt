package com.dpm.sixpack.data.source.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendNotificationRequestDto(
    @SerialName("userId")
    val userId: Long,
)
