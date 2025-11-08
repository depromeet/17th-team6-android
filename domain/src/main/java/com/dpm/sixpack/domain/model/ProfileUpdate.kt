package com.dpm.sixpack.domain.model

import java.io.File

/**
 * 프로필 수정 요청 모델
 */
data class ProfileUpdateRequest(
    val nickname: String,
    val imageOption: ProfileImageOption,
    val profileImage: File? = null,
)

/**
 * 프로필 수정 응답 모델
 */
data class ProfileUpdateResponse(
    val profileImageUrl: String?,
)
