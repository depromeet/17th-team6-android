package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.model.SmsVerificationResult
import com.dpm.sixpack.domain.repository.AuthRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class VerifySmsCodeUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        phoneNumber: String,
        verificationCode: String,
    ): DoRunResult<SmsVerificationResult> = authRepository.verifySmsCode(phoneNumber, verificationCode)
}
