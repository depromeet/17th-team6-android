package com.dpm.sixpack.domain.repository

import android.graphics.Bitmap
import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.model.RunSession
import com.dpm.sixpack.domain.model.RunningSessionResult
import com.dpm.sixpack.domain.usecase.running.SaveRealtimeRunningDataResult
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
        mapImage: Bitmap,
    ): DoRunResult<Long>

    suspend fun finish(sessionId: Long): DoRunResult<RunningSessionResult>

    suspend fun getRunSessions(
        isSelfied: Boolean?,
        startDateTime: String?,
        endDateTime: String?,
    ): DoRunResult<List<RunSession>>
}
