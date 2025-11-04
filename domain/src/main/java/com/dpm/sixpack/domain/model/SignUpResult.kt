package com.dpm.sixpack.domain.model

data class SignUpResult(
    val user: AuthUser,
    val token: AuthToken,
)
