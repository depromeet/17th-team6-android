package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.repository.AuthRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class SendSmsCodeUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(phoneNumber: String): DoRunResult<Unit> = authRepository.sendSmsCode(phoneNumber)
}
