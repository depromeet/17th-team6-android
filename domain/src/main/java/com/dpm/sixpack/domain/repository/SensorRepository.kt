package com.dpm.sixpack.domain.repository

import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.flow.Flow

interface SensorRepository {
    val totalStep: Flow<DoRunResult<Int>>
}
