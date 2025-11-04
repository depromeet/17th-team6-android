package com.dpm.sixpack.data.source.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequestDto(
    @SerialName("nickname") val nickname: String,
    @SerialName("phoneNumber") val phoneNumber: String,
)
