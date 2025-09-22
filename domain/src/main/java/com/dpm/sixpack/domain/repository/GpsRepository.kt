package com.dpm.sixpack.domain.repository

import android.location.Location
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.flow.Flow

interface GpsRepository {
    fun getLocationFlow(): Flow<DoRunResult<Location>>
}
