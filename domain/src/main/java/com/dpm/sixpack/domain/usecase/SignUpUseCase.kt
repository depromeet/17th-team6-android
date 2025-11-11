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
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(
            nickname: String,
            phoneNumber: String,
            profileImage: File?,
            marketingConsentAt: String?,
            locationConsentAt: String?,
            personalConsentAt: String,
        ): DoRunResult<SignUpResult> {
            val fcmToken = userRepository.getFcmDeviceToken()

            val result =
                authRepository.signUp(
                    nickname = nickname,
                    phoneNumber = phoneNumber,
                    profileImage = profileImage,
                    marketingConsentAt = marketingConsentAt,
                    locationConsentAt = locationConsentAt,
                    personalConsentAt = personalConsentAt,
                    deviceToken = fcmToken,
                )

            // 회원가입 성공 시 userId, token 저장
            result.onSuccess { signUpResult ->
                userRepository.updateUserId(signUpResult.user.id)
                userRepository.updateAccessToken(signUpResult.token.accessToken)
                userRepository.updateRefreshToken(signUpResult.token.refreshToken)
            }

            return result
        }
    }
