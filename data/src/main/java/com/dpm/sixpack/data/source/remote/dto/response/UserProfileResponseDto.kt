package com.dpm.sixpack.data.source.remote.dto.response

import com.dpm.sixpack.domain.model.UserProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 사용자 프로필 조회 응답 DTO
 */
@Serializable
data class UserProfileResponseDto(
    @SerialName("id")
    val id: Long,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("profileImageUrl")
    val profileImageUrl: String? = null,
    @SerialName("code")
    val code: String,
    @SerialName("phoneNumberFormatted")
    val phoneNumberFormatted: String,
    @SerialName("createdAt")
    val createdAt: String,
) {
    fun toUserProfile() =
        UserProfile(
            id = id,
            nickname = nickname,
            profileImageUrl = profileImageUrl,
            code = code,
            phoneNumberFormatted = phoneNumberFormatted,
            createdAt = createdAt,
        )
}
