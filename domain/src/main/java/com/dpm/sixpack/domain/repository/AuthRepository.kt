package com.dpm.sixpack.domain.repository

import com.dpm.sixpack.domain.model.AuthToken
import com.dpm.sixpack.domain.model.SignUpResult
import com.dpm.sixpack.domain.model.SmsVerificationResult
import com.dpm.sixpack.domain.util.DoRunResult
import java.io.File

interface AuthRepository {
    suspend fun sendSmsCode(phoneNumber: String): DoRunResult<Unit>

    suspend fun verifySmsCode(
        phoneNumber: String,
        verificationCode: String,
    ): DoRunResult<SmsVerificationResult>

    suspend fun signUp(
        nickname: String,
        phoneNumber: String,
        profileImage: File?,
    ): DoRunResult<SignUpResult>

    suspend fun refreshToken(refreshToken: String): DoRunResult<AuthToken>
}
