package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.repository.RunningSessionRepository
import javax.inject.Inject

class StartRunningUseCase @Inject constructor(
    private val repository: RunningSessionRepository
) {
    suspend operator fun invoke(goalPlanId: Long): Long {
        repository.start(goalPlanId)
        return 1L
    }
}
