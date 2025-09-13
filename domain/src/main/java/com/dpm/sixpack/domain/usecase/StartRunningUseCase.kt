package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.repository.RunningSessionRepository
import javax.inject.Inject

class StartRunningUseCase @Inject constructor(
    private val repository: RunningSessionRepository
) {
    suspend operator fun invoke(): Long {
        repository.start()
        return 1L
    }
}
