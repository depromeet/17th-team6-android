package com.dpm.sixpack.data.repository

import com.dpm.sixpack.domain.repository.SensorRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MockSensorRepository
    @Inject
    constructor() : SensorRepository {
        override fun getTotalStep(): Flow<DoRunResult<Int>> =
            flow {
                var totalSteps = 0
                while (true) {
                    emit(DoRunResult.Success(totalSteps))
                    totalSteps += 3
                    delay(1000L)
                }
            }
    }
