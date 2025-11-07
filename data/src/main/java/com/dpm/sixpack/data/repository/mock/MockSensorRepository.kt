package com.dpm.sixpack.data.repository.mock

import com.dpm.sixpack.domain.repository.SensorRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MockSensorRepository @Inject constructor() : SensorRepository {
    override val totalStep: Flow<DoRunResult<Int>> =
        flow {
            var totalSteps = 0
            while (true) {
                // 1초마다 3걸음씩 증가하는 걸음 수를 방출
                emit(DoRunResult.Success(totalSteps))
                totalSteps += 3
                delay(1000L)
            }
        }
}
