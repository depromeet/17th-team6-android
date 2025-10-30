package com.dpm.sixpack.data.repository

import com.dpm.sixpack.data.source.local.database.LocalRunningDataSource
import com.dpm.sixpack.data.source.local.database.toRealtimeRunningData
import com.dpm.sixpack.data.source.local.database.toTrackPointEntity
import com.dpm.sixpack.data.source.remote.datasoruce.RunningSessionDataSource
import com.dpm.sixpack.data.source.remote.dto.request.FinishRunningRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.SaveSegmentDataRequestsDto
import com.dpm.sixpack.data.source.remote.dto.request.toDto
import com.dpm.sixpack.data.source.remote.dto.request.toSegmentDataDto
import com.dpm.sixpack.data.source.remote.dto.response.toRunningSessionResult
import com.dpm.sixpack.data.source.remote.dto.response.toSyncResult
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.MaxPaceData
import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.model.RunningSessionResult
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.usecase.SaveRealtimeRunningDataResult
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class RunningSessionRepositoryImpl @Inject constructor(
    private val runningSessionDataSource: RunningSessionDataSource,
    private val localRunningDataSource: LocalRunningDataSource,
) : RunningSessionRepository {
    override suspend fun startSession(): DoRunResult<Long> =
        withContext(Dispatchers.IO) {
            try {
                // 정상적인 2xx 응답 시도
                val response = runningSessionDataSource.postStartSession()
                val sessionId =
                    response.data?.sessionId
                        ?: throw DoRunException.DataError("서버로부터 세션 ID를 받지 못했습니다.")

                DoRunResult.Success(sessionId)
            } catch (e: HttpException) {
                // [개선] 400 에러(세션 충돌) 처리를 별도 함수로 분리
                if (e.code() == 400) {
                    handleSessionConflict(e)
                } else {
                    DoRunResult.Failure(DoRunException.DataError("${e.code()} HTTP 에러: ${e.message}"))
                }
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("네트워크 요청에 실패했습니다: ${e.message}"))
            }
        }

    /**
     * startSession의 400 에러(진행 중인 세션)를 파싱하고 처리하는 private 함수
     */
    private fun handleSessionConflict(e: HttpException): DoRunResult<Long> =
        try {
            val errorBodyString = e.response()?.errorBody()?.string()
            if (errorBodyString != null) {
                val errorResponse =
                    Json.decodeFromString<BaseResponse<Nothing>>(errorBodyString)

                // message에서 sessionId 파싱
                val regex = Regex("""\[runSessionId: (\d+)]""")
                val matchResult = regex.find(errorResponse.message)
                val sessionId =
                    matchResult
                        ?.groups
                        ?.get(1)
                        ?.value
                        ?.toLongOrNull()

                if (sessionId != null) {
                    // 400 에러지만, 진행 중인 세션 ID를 찾았으므로 성공 처리
                    DoRunResult.Success(sessionId)
                } else {
                    DoRunResult.Failure(DoRunException.DataError("400 에러: ${errorResponse.message}"))
                }
            } else {
                DoRunResult.Failure(DoRunException.DataError("400 에러: 응답 본문이 없습니다."))
            }
        } catch (parseException: Exception) {
            DoRunResult.Failure(DoRunException.DataError("400 에러: 응답 본문 파싱 실패: ${parseException.message}"))
        }

    override suspend fun saveRealtimeDataOnLocal(
        data: RealtimeRunningData,
    ): DoRunResult<SaveRealtimeRunningDataResult.LocalResult> =
        withContext(Dispatchers.IO) {
            try {
                val trackPoint = data.toTrackPointEntity()
                localRunningDataSource.saveRunningTrackPoint(trackPoint)
                Timber.d("Realtime data saved to local: $trackPoint")

                DoRunResult.Success(SaveRealtimeRunningDataResult.LocalResult)
            } catch (e: Exception) {
                DoRunResult.Failure(
                    DoRunException.DatabaseError(
                        "Room DB 저장 실패: ${e.message}",
                    ),
                )
            }
        }

    override suspend fun getLastRunningDataOnLocal(): DoRunResult<RealtimeRunningData> =
        withContext(Dispatchers.IO) {
            try {
                val lastPoint = localRunningDataSource.getLastRunningTrackPoint()
                if (lastPoint != null) {
                    DoRunResult.Success(lastPoint.toRealtimeRunningData())
                } else {
                    DoRunResult.Failure(DoRunException.DatabaseError("마지막 러닝 데이터가 없습니다."))
                }
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DatabaseError("마지막 러닝 데이터 가져오기 실패: ${e.message}"))
            }
        }

    override suspend fun syncSegmentData(
        sessionId: Long,
        isPaused: Boolean,
    ): DoRunResult<SaveRealtimeRunningDataResult.SyncResult> =
        withContext(Dispatchers.IO) {
            try {
                // 로컬 DB에서 동기화되지 않은 포인트 가져옴
                val unsyncedPoints = localRunningDataSource.getUnsyncedTrackPoints()

                // 보낼 데이터가 없는 경우, 성공으로 간주하고 일찍 반환
                if (unsyncedPoints.isEmpty()) {
                    Timber.d("saveSegmentData: No unsynced points to send.")
                    return@withContext DoRunResult.Success(SaveRealtimeRunningDataResult.SyncResult())
                }
                Timber.d("saveSegmentData: Found ${unsyncedPoints.size} unsynced points to send.")

                // 서버에 데이터 전송
                val response =
                    runningSessionDataSource.postSegmentData(
                        sessionId,
                        SaveSegmentDataRequestsDto(
                            segments = unsyncedPoints.map { it.toSegmentDataDto() },
                            isStopped = isPaused,
                        ),
                    )

                // 응답 데이터 변환
                val syncResult =
                    response.data?.toSyncResult()
                        ?: throw DoRunException.DataError("서버 응답 데이터(sync)가 비어 있습니다.")

                // 전송 성공 시, 로컬 DB에 '동기화됨' 표시
                val pointIds = unsyncedPoints.map { it.id }
                localRunningDataSource.markPointsAsSynced(pointIds)
                Timber.d("Segment data synced successfully for ${pointIds.size} points.")

                // 성공 결과 반환
                DoRunResult.Success(syncResult)
            } catch (e: HttpException) {
                // 서버 에러 처리 (실패 시, isSynced=0으로 남아있어 다음 시도 가능)
                Timber.w("Server error on saveSegmentData: ${e.code()} ${e.message}")
                DoRunResult.Failure(DoRunException.ServerError(e.code(), e.message.toString()))
            } catch (e: Exception) {
                // 네트워크, DB, 직렬화 등 모든 예외 처리
                Timber.e(e, "Failed to saveSegmentData")
                DoRunResult.Failure(DoRunException.DataError("세그먼트 동기화 실패: ${e.message}"))
            }
        }

    /**
     * 1. 남은 러닝 데이터 서버 전송
     * 2. 종료 API 호출
     * 3. 로컬 DB 지우기
     */
    override suspend fun finishSession(
        sessionId: Long,
        mapImageUrl: String,
    ): DoRunResult<RunningSessionResult> =
        withContext(Dispatchers.IO) {
            try {
                Timber.d("Finishing run: Syncing final segments...")
                val segmentSyncResult = syncSegmentData(sessionId, false)
                if (segmentSyncResult is DoRunResult.Failure) {
                    Timber.w("Failed to sync final segment before finishing.")
                }

                // DTO 생성 로직 (예외 발생 가능)
                val finishRequestDto = getFinishRequestDto(mapImageUrl)

                // API 호출 (예외 발생 가능)
                val runningSessionResult = requestFinishApi(sessionId, finishRequestDto)

                // API 호출 성공 시 로컬 데이터 삭제
                localRunningDataSource.deleteAllRunningTrackPoints()
                Timber.d("Successfully finished session. Local data cleared.")

                // 최종 성공 반환
                DoRunResult.Success(runningSessionResult)
            } catch (e: HttpException) {
                // (syncSegmentData, requestFinishApi에서 발생한) 서버 에러
                Timber.w("Server error on finish: ${e.code()} ${e.message}")
                DoRunResult.Failure(DoRunException.ServerError(e.code(), e.message.toString()))
            } catch (e: Exception) {
                // (getFinishRequestDto, requestFinishApi, syncSegmentData에서 발생한)
                // DatabaseError, DataError, 기타 예외
                Timber.e(e, "Failed to finish running session")
                DoRunResult.Failure(DoRunException.DataError("러닝 종료 실패: ${e.message}"))
            }
        }

    /**
     * 종료 API를 호출하고 응답을 파싱/검증하는 private 함수
     * @throws DoRunException.DataError 서버 응답 데이터가 null일 경우
     * @throws HttpException 서버가 non-2xx 응답을 반환할 경우
     */
    private suspend fun requestFinishApi(
        sessionId: Long,
        finishRequestDto: FinishRunningRequestDto,
    ): RunningSessionResult =
        try {
            val finishResponse =
                runningSessionDataSource.postFinishRunning(
                    sessionId,
                    finishRequestDto,
                )
            finishResponse.data?.toRunningSessionResult()
                ?: throw DoRunException.DataError("서버 응답 데이터(finish)가 비어 있습니다.")
        } catch (e: HttpException) {
            // 서버 에러
            Timber.w("Server error on requestFinishApi: ${e.code()} ${e.message}")
            throw DoRunException.ServerError(e.code(), e.message.toString())
        } catch (e: Exception) {
            // 데이터 파싱 에러(DataError) 등
            Timber.e(e, "Failed on requestFinishApi")
            throw DoRunException.DataError("종료 API 요청 실패: ${e.message}")
        }

    private suspend fun getFinishRequestDto(mapImageUrl: String): FinishRunningRequestDto {
        val lastTrackPoint =
            localRunningDataSource.getLastRunningTrackPoint()
                ?: throw DoRunException.DatabaseError("저장된 로컬 데이터가 없습니다.")

        val runningSessionResultDto =
            RunningSessionResult(
                totalDistanceMeter = lastTrackPoint.distanceInMeter,
                totalDurationSec = lastTrackPoint.durationInSec,
                avgPace = lastTrackPoint.avgPace,
                maxPace =
                    MaxPaceData(
                        lastTrackPoint.maxPace,
                        lastTrackPoint.maxPaceLatitude,
                        lastTrackPoint.maxPaceLongitude,
                    ),
                avgCadence = lastTrackPoint.avgCadence,
                maxCadence = lastTrackPoint.maxCadence,
            ).toDto()

        // 종료 API 호출
        Timber.d("Final segments synced. Calling finish API...")
        return FinishRunningRequestDto(
            data = runningSessionResultDto,
            mapImage = mapImageUrl,
        )
    }
}
