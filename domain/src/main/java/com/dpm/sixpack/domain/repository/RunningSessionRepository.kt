package com.dpm.sixpack.domain.repository

import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.model.RunningSessionResult
import com.dpm.sixpack.domain.usecase.SaveRealtimeRunningDataResult
import com.dpm.sixpack.domain.util.DoRunResult

interface RunningSessionRepository {

//    suspend fun start(): DoRunResult<Unit>

    suspend fun saveRealtimeData(data: RealtimeRunningData): DoRunResult<SaveRealtimeRunningDataResult.LocalResult>

    suspend fun saveSegmentData(sessionId: Long): DoRunResult<SaveRealtimeRunningDataResult.SyncResult>

    suspend fun finish(sessionId: Long): DoRunResult<RunningSessionResult>
}

