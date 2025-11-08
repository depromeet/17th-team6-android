package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.model.SignUpResult
import com.dpm.sixpack.domain.repository.AuthRepository
import com.dpm.sixpack.domain.repository.UserRepository
import com.dpm.sixpack.domain.util.DoRunResult
import java.io.File
import javax.inject.Inject

class SignUpUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val userPreferenceRepository: UserRepository,
    ) {
        suspend operator fun invoke(
            nickname: String,
            phoneNumber: String,
            profileImage: File?,
            marketingConsentAt: String?,
            locationConsentAt: String?,
            personalConsentAt: String,
            deviceToken: String?,
        ): DoRunResult<SignUpResult> {
            val result =
                authRepository.signUp(
                    nickname = nickname,
                    phoneNumber = phoneNumber,
                    profileImage = profileImage,
                    marketingConsentAt = marketingConsentAt,
                    locationConsentAt = locationConsentAt,
                    personalConsentAt = personalConsentAt,
                    deviceToken = deviceToken,
                )

            // 회원가입 성공 시 userId, token 저장
            result.onSuccess { signUpResult ->
                userPreferenceRepository.updateUserId(signUpResult.user.id)
                userPreferenceRepository.updateAccessToken(signUpResult.token.accessToken)
                userPreferenceRepository.updateRefreshToken(signUpResult.token.refreshToken)
            }

            return result
        }
    }
