package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.repository.SensorRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetStepCountUseCase @Inject constructor(
    private val sensorRepository: SensorRepository,
) {
    operator fun invoke(): Flow<DoRunResult<Int>> = sensorRepository.totalStep.onStart { emit(DoRunResult.Success(0)) }
}
