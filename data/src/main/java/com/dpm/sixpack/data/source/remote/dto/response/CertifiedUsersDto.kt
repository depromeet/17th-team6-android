package com.dpm.sixpack.data.source.remote.dto.response

import com.dpm.sixpack.domain.model.CertifiedUser
import com.dpm.sixpack.domain.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CertifiedUsersDto(
    @SerialName("users")
    val users: List<CertifiedUserDto>
) {
    fun toDomain(): List<CertifiedUser> = users.map { it.toDomain() }
}

@Serializable
data class CertifiedUserDto(
    @SerialName("userId")
    val userId: Long,
    @SerialName("userName")
    val userName: String,
    @SerialName("userImageUrl")
    val userImageUrl: String,
    @SerialName("postingTime")
    val postingTime: String,
    @SerialName("isMe")
    val isMe: Boolean
) {
    fun toDomain(): CertifiedUser =
        CertifiedUser(
            user = User(
                userId = userId,
                nickName = userName,
                profileImgUrl = userImageUrl,
                isMe = isMe
            ),
            postingTime = postingTime,
        )
}
