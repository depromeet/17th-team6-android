package com.dpm.sixpack.domain.repository

import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.model.RunningSessionResult
import com.dpm.sixpack.domain.usecase.SaveRealtimeRunningDataResult
import com.dpm.sixpack.domain.util.DoRunResult

interface RunningSessionRepository {
    suspend fun start(): DoRunResult<Long>

    suspend fun saveRealtimeDataOnLocal(
        data: RealtimeRunningData,
    ): DoRunResult<SaveRealtimeRunningDataResult.LocalResult>

    suspend fun saveSegmentData(
        sessionId: Long,
        isPaused: Boolean,
    ): DoRunResult<SaveRealtimeRunningDataResult.SyncResult>

    suspend fun finish(sessionId: Long): DoRunResult<RunningSessionResult>
}
