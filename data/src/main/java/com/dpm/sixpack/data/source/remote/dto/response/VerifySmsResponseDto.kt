package com.dpm.sixpack.data.source.remote.dto.response

import com.dpm.sixpack.domain.model.SmsVerificationResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifySmsResponseDto(
    @SerialName("phoneNumber") val phoneNumber: String,
    @SerialName("isExistingUser") val isExistingUser: Boolean,
    @SerialName("user") val user: UserDto? = null,
    @SerialName("token") val token: TokenDto? = null,
) {
    fun toSmsVerificationResult() =
        SmsVerificationResult(
            phoneNumber = phoneNumber,
            isExistingUser = isExistingUser,
            user = user?.toAuthUser(),
            token = token?.toAuthToken(),
        )
}
