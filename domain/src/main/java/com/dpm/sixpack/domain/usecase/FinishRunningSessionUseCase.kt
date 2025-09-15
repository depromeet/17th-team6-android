package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.model.RunningSessionResult
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.util.DoRunResult
import javax.inject.Inject

class FinishRunningSessionUseCase
    @Inject
    constructor(
        private val repository: RunningSessionRepository,
    ) {
        suspend operator fun invoke(sessionId: Long): DoRunResult<RunningSessionResult> =
            repository.finish()
    }
