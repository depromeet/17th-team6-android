package com.dpm.sixpack.data.source.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendDeleteRequestDto(
    @SerialName("friendIds")
    val friendIds: List<Long>,
)
