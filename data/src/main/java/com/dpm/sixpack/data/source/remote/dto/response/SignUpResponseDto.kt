package com.dpm.sixpack.data.source.remote.dto.response

import com.dpm.sixpack.domain.model.AuthToken
import com.dpm.sixpack.domain.model.AuthUser
import com.dpm.sixpack.domain.model.SignUpResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponseDto(
    @SerialName("user") val user: UserDto,
    @SerialName("token") val token: TokenDto,
) {
    fun toSignUpResult() =
        SignUpResult(
            user = user.toAuthUser(),
            token = token.toAuthToken(),
        )
}

@Serializable
data class UserDto(
    @SerialName("id") val id: Long,
    @SerialName("nickname") val nickname: String,
) {
    fun toAuthUser() =
        AuthUser(
            id = id,
            nickname = nickname,
        )
}

@Serializable
data class TokenDto(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
) {
    fun toAuthToken() =
        AuthToken(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
}
