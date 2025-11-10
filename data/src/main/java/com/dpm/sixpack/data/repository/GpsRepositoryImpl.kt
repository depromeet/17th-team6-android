package com.dpm.sixpack.data.repository

import android.location.Location
import com.dpm.sixpack.data.source.local.gps.GpsDataSource
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.repository.GpsRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GpsRepositoryImpl @Inject constructor(
    gpsDataSource: GpsDataSource,
) : GpsRepository {
    override val locationFlow: Flow<DoRunResult<Location>> =
        gpsDataSource.locationFlow
            .map { location ->
                DoRunResult.Success(location) as DoRunResult<Location>
            }.catch { throwable ->
                emit(
                    DoRunResult.Failure(
                        DoRunException.DataError(throwable.message ?: " repository : location 정보가 존재하지않습니다."),
                    ),
                )
            }
}
