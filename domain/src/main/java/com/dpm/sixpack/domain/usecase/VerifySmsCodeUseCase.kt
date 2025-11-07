package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.model.SmsVerificationResult
import com.dpm.sixpack.domain.repository.AuthRepository
import com.dpm.sixpack.domain.repository.UserRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class VerifySmsCodeUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val userPreferenceRepository: UserRepository,
    ) {
        suspend operator fun invoke(
            phoneNumber: String,
            verificationCode: String,
        ): DoRunResult<SmsVerificationResult> {
            val result = authRepository.verifySmsCode(phoneNumber, verificationCode)

            // 인증 성공 시 userId와 token 저장
            result.onSuccess { verificationResult ->
                verificationResult.user?.let { user ->
                    userPreferenceRepository.updateUserId(user.id)
                }
                verificationResult.token?.let { token ->
                    userPreferenceRepository.updateAccessToken(token.accessToken)
                    userPreferenceRepository.updateRefreshToken(token.refreshToken)
                }
            }

            return result
        }
    }
