package com.dpm.sixpack.domain.model

/**
 * 사용자 프로필 정보 모델
 */
data class UserProfile(
    val id: Long,
    val nickname: String,
    val profileImageUrl: String?,
    val code: String,
    val phoneNumberFormatted: String,
    val createdAt: String,
)
