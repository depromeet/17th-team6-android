package com.dpm.sixpack.data.repository

import com.dpm.sixpack.data.repository.di.MockRequestDataFactory
import com.dpm.sixpack.data.source.remote.datasoruce.RunningSessionDataSource
import com.dpm.sixpack.data.source.remote.dto.request.StartRunningRequestDto
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.model.RunSession
import com.dpm.sixpack.domain.model.RunningSessionResult
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.usecase.SaveRealtimeRunningDataResult
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RunningSessionRepositoryImpl @Inject constructor(
    private val runningSessionDataSource: RunningSessionDataSource,
) : RunningSessionRepository {
    override suspend fun start(goalPlanId: Long): DoRunResult<Long> =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    runningSessionDataSource.postStartRunning(StartRunningRequestDto(goalPlanId))

                val sessionId =
                    response.data?.sessionId
                        ?: throw DoRunException.DataError("서버로부터 세션 ID를 받지 못했습니다.")

                DoRunResult.Success(sessionId)
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("네트워크 요청에 실패했습니다: ${e.message}"))
            }
        }

    override suspend fun saveRealtimeData(
        data: RealtimeRunningData,
    ): DoRunResult<SaveRealtimeRunningDataResult.LocalResult> {
        TODO("Not yet implemented")
    }

    override suspend fun saveSegmentData(sessionId: Long): DoRunResult<SaveRealtimeRunningDataResult.SyncResult> =
        withContext(Dispatchers.IO) {
            try {
                // TODO: Mock 데이터 대신 Room DB에서  데이터를 가져와 전송
                val response =
                    runningSessionDataSource.postSegmentData(
                        sessionId,
                        MockRequestDataFactory.createMockSaveSegmentRequest(),
                    )
                val syncResult =
                    response.data?.toSyncResult()
                        ?: throw DoRunException.DataError("서버 응답 데이터가 비어 있습니다.")
                DoRunResult.Success(syncResult)
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("네트워크 요청에 실패했습니다: ${e.message}"))
            }
        }

    override suspend fun finish(sessionId: Long): DoRunResult<RunningSessionResult> =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    runningSessionDataSource.postFinishRunning(
                        sessionId,
                        MockRequestDataFactory.createMockFinishRunningRequest(),
                    )
                val runningSessionResult =
                    response.data?.toRunningSessionResult()
                        ?: throw DoRunException.DataError("서버 응답 데이터가 비어 있습니다.")

                DoRunResult.Success(runningSessionResult)
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("네트워크 요청에 실패했습니다: ${e.message}"))
            }
        }

    override suspend fun getRunSessions(
        isSelfied: Boolean?,
        startDateTime: String?,
    ): DoRunResult<List<RunSession>> =
        withContext(Dispatchers.IO) {
            try {
                val response = runningSessionDataSource.getRunSessions(isSelfied, startDateTime)
                val runSessions =
                    response.data?.map { it.toRunSession() }
                        ?: throw DoRunException.DataError("서버 응답 데이터가 비어 있습니다.")

                DoRunResult.Success(runSessions)
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("네트워크 요청에 실패했습니다: ${e.message}"))
            }
        }
}
