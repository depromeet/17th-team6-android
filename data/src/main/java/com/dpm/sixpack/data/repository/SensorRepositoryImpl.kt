package com.dpm.sixpack.data.repository

import com.dpm.sixpack.data.source.local.sensor.SensorDataSource
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.repository.SensorRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SensorRepositoryImpl @Inject constructor(
    private val sensorDataSource: SensorDataSource,
) : SensorRepository {
    override fun getTotalStep(): Flow<DoRunResult<Long>> =
        sensorDataSource
            .getTotalStepsFlow()
            .map { stepCount ->
                DoRunResult.Success(stepCount)
            }.catch { throwable ->
                DoRunResult.Failure(
                    DoRunException.DataError(
                        throwable.message ?: " repository : totalStep이 존재하지 않습니다.",
                    ),
                )
            }
}
