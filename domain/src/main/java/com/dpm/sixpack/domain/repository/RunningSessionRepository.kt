package com.dpm.sixpack.domain.repository

import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.model.RunningSessionResult
import com.dpm.sixpack.domain.usecase.SaveRealtimeRunningDataResult
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.flow.Flow

interface RunningSessionRepository {
    suspend fun start(goalPlanId: Long): DoRunResult<Long>

    fun getRealtimeData(): Flow<DoRunResult<RealtimeRunningData>>

    suspend fun saveRealtimeData(data: RealtimeRunningData): DoRunResult<SaveRealtimeRunningDataResult.LocalResult>

    suspend fun saveSegmentData(): DoRunResult<SaveRealtimeRunningDataResult.SyncResult>

    suspend fun finish(): DoRunResult<RunningSessionResult>
}
