package com.dpm.sixpack.data.source.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequestDto(
    @SerialName("phoneNumber") val phoneNumber: String,
    @SerialName("nickname") val nickname: String,
    @SerialName("consent") val consent: ConsentDto,
    @SerialName("deviceToken") val deviceToken: String?,
)
