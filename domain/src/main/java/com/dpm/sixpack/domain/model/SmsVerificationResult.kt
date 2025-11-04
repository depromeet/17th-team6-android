package com.dpm.sixpack.domain.model

data class SmsVerificationResult(
    val phoneNumber: String,
    val isExistingUser: Boolean,
    val user: AuthUser?,
    val token: AuthToken?,
)
