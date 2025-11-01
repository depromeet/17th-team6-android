package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.model.SignUpResult
import com.dpm.sixpack.domain.repository.AuthRepository
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import com.dpm.sixpack.domain.util.DoRunResult
import java.io.File
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
) {
    suspend operator fun invoke(
        nickname: String,
        phoneNumber: String,
        profileImage: File?,
    ): DoRunResult<SignUpResult> {
        val result = authRepository.signUp(nickname, phoneNumber, profileImage)

        // 회원가입 성공 시 온보딩 완료 처리
        result.onSuccess {
            userPreferenceRepository.updateOnboardingComplete(true)
        }

        return result
    }
}
