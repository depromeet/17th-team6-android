package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.model.AuthEvent
import com.dpm.sixpack.domain.repository.AuthRepository
import com.dpm.sixpack.domain.repository.UserRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class LogoutUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val userPreferenceRepository: UserRepository,
    ) {
        suspend operator fun invoke(): DoRunResult<Unit> {
            val result = authRepository.logout()

            // 로그아웃 성공 시 토큰 클리어 및 AuthEvent emit
            result.onSuccess {
                userPreferenceRepository.clearTokens()
                userPreferenceRepository.emitAuthEvent(AuthEvent.LoggedOut)
            }

            return result
        }
    }
