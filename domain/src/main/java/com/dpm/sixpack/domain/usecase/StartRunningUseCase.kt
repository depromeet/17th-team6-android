package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class StartRunningUseCase
    @Inject
    constructor(
        private val repository: RunningSessionRepository,
    ) {
        suspend operator fun invoke(goalPlanId: Long): DoRunResult<Long> = repository.start(goalPlanId)
    }
