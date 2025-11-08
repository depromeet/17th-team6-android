package com.dpm.sixpack.data.source.remote.dto.response

import com.dpm.sixpack.domain.model.ProfileUpdateResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 프로필 수정 응답 DTO
 */
@Serializable
data class MyProfileUpdateResponseDto(
    @SerialName("profileImageUrl")
    val profileImageUrl: String? = null,
) {
    fun toProfileUpdateResponse() =
        ProfileUpdateResponse(
            profileImageUrl = profileImageUrl,
        )
}
