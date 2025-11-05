package com.dpm.sixpack.data.source.remote.dto.response

import com.dpm.sixpack.domain.model.AuthToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponseDto(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
) {
    fun toAuthToken() =
        AuthToken(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
}
