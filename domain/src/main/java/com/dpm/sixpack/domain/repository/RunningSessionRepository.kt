package com.dpm.sixpack.domain.repository

import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.model.RunningSessionResult
import com.dpm.sixpack.domain.usecase.SaveRealtimeRunningDataResult
import com.dpm.sixpack.domain.util.DoRunResult

interface RunningSessionRepository {
    suspend fun startSession(): DoRunResult<Long>

    suspend fun saveRealtimeDataOnLocal(
        data: RealtimeRunningData,
    ): DoRunResult<SaveRealtimeRunningDataResult.LocalResult>

    suspend fun getLastRunningDataOnLocal(): DoRunResult<RealtimeRunningData>

    suspend fun syncSegmentData(
        sessionId: Long,
        isPaused: Boolean,
    ): DoRunResult<SaveRealtimeRunningDataResult.SyncResult>

    suspend fun finishSession(
        sessionId: Long,
        mapImageUrl: String,
    ): DoRunResult<RunningSessionResult>
}
