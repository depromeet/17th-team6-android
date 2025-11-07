package com.dpm.sixpack.data.source.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 프로필 수정 요청 DTO
 * multipart/form-data의 data 파트로 전송됨
 */
@Serializable
data class MyProfileUpdateRequestDto(
    @SerialName("nickname")
    val nickname: String,
    @SerialName("imageOption")
    val imageOption: String, // SET, REMOVE, KEEP
)
