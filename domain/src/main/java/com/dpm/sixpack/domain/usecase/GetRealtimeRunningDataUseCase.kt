package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRealtimeRunningDataUseCase
    @Inject
    constructor(
        private val repository: RunningSessionRepository,
    ) {
        suspend operator fun invoke(): Flow<DoRunResult<RealtimeRunningData>> = repository.getRealtimeData()
    }
