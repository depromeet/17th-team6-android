package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.model.SmsVerificationResult
import com.dpm.sixpack.domain.repository.AuthRepository
import com.dpm.sixpack.domain.repository.UserRepository
import com.dpm.sixpack.domain.util.DoRunResult
import timber.log.Timber
import javax.inject.Inject

class VerifySmsCodeUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferenceRepository: UserRepository,
) {
    suspend operator fun invoke(
        phoneNumber: String,
        verificationCode: String,
    ): DoRunResult<SmsVerificationResult> {
        val result = authRepository.verifySmsCode(phoneNumber, verificationCode)

        // 인증 성공 시 userId와 token 저장
        if (result is DoRunResult.Success) {
            val verificationResult = result.data // result.data는 SmsVerificationResult

            // --- 1. 이전 코드 실행 ---
            // 이 작업들이 (suspend 함수라고 가정) 순차적으로 완료될 때까지 대기합니다.
            verificationResult.user?.let { user ->
                userPreferenceRepository.updateUserId(user.id)
            }
            verificationResult.token?.let { token ->
                userPreferenceRepository.updateAccessToken(token.accessToken)
                userPreferenceRepository.updateRefreshToken(token.refreshToken)
            }

            // --- 2. 이전 코드가 모두 완료된 후 실행 ---
            // 위 let 블록 안의 suspend 함수들이 모두 완료된 후에야 이 코드가 실행됩니다.
            userPreferenceRepository
                .postNewFcmToken()
                .onSuccess {
                    Timber.d("VerifySmsCodeUseCase: FCM 토큰 저장 성공")
                }.onError {
                    Timber.e("VerifySmsCodeUseCase: FCM 토큰 저장 실패")
                }
        }

        return result
    }
}
