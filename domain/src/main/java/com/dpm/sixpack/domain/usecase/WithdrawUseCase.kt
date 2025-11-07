package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.model.AuthEvent
import com.dpm.sixpack.domain.repository.AuthRepository
import com.dpm.sixpack.domain.repository.UserRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class WithdrawUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val userPreferenceRepository: UserRepository,
    ) {
        suspend operator fun invoke(): DoRunResult<Unit> {
            val result = authRepository.withdraw()

            // 회원 탈퇴 성공 시 토큰 및 세션 클리어, AuthEvent emit
            result.onSuccess {
                userPreferenceRepository.clearTokens()
                userPreferenceRepository.clearSessionId()
                userPreferenceRepository.emitAuthEvent(AuthEvent.LoggedOut)
            }

            return result
        }
    }
