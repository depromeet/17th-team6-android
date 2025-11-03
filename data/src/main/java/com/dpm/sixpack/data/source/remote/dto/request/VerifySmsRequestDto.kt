package com.dpm.sixpack.data.source.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifySmsRequestDto(
    @SerialName("phoneNumber") val phoneNumber: String,
    @SerialName("verificationCode") val verificationCode: String,
)
