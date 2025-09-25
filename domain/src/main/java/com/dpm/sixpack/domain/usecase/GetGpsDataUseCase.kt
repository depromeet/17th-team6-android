package com.dpm.sixpack.domain.usecase

import android.location.Location
import com.dpm.sixpack.domain.repository.GpsRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetGpsDataUseCase @Inject constructor(
    private val gpsRepository: GpsRepository,
) {
    operator fun invoke(): Flow<DoRunResult<Location>> = gpsRepository.getLocationFlow()
}
