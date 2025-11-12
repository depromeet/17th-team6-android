package com.dpm.sixpack.data.source.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteFriendResponseDto(
    @SerialName("deletedFriends")
    val deletedFriends: Map<String, String> = emptyMap(),
)
