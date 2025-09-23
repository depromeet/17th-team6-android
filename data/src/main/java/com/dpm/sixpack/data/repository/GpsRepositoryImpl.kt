package com.dpm.sixpack.data.repository

import android.location.Location
import com.dpm.sixpack.data.source.local.gps.LocationDataSource
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.repository.GpsRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GpsRepositoryImpl @Inject constructor(
    private val locationDataSource: LocationDataSource,
) : GpsRepository {
    override fun getLocationFlow(): Flow<DoRunResult<Location>> =
        locationDataSource
            .getLocationFlow()
            .map { location ->
                DoRunResult.Success(location)
            }.catch { throwable ->
                DoRunResult.Failure(
                    DoRunException.DataError(throwable.message ?: " repository : location 정보가 존재하지않습니다."),
                )
            }
}
