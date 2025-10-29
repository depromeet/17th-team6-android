package com.dpm.sixpack.data.repository

import com.dpm.sixpack.data.source.local.database.LocalRunningDataSource
import com.dpm.sixpack.data.source.local.database.toTrackPointEntity
import com.dpm.sixpack.data.source.remote.datasoruce.RunningSessionDataSource
import com.dpm.sixpack.data.source.remote.dto.request.FinishRunningRequestData
import com.dpm.sixpack.data.source.remote.dto.request.FinishRunningRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.SaveSegmentDataRequestsDto
import com.dpm.sixpack.data.source.remote.dto.request.toSegmentDataDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import com.dpm.sixpack.domain.exception.DoRunException
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
    override suspend fun start(): DoRunResult<Long> =
        withContext(Dispatchers.IO) {
            try {
                val response = runningSessionDataSource.postStartSession()
                val sessionId =
                    response.data?.sessionId
                        ?: throw DoRunException.DataError("서버로부터 세션 ID를 받지 못했습니다.")

                DoRunResult.Success(sessionId)
            } catch (e: HttpException) {
                // HTTP 에러 (non-2xx) 발생 시
                if (e.code() == 400) {
                    // 5. 400 에러인 경우, 에러 본문(errorBody)을 파싱
                    try {
                        val errorBodyString = e.response()?.errorBody()?.string()
                        if (errorBodyString != null) {
                            // 6. Json 파서를 사용해 에러 응답 객체로 변환
                            val errorResponse =
                                Json.decodeFromString<BaseResponse<Nothing>>(errorBodyString)

                            val regex = Regex("""\[runSessionId: (\d+)]""")
                            val matchResult = regex.find(errorResponse.message)
                            val sessionId =
                                matchResult
                                    ?.groups
                                    ?.get(1)
                                    ?.value
                                    ?.toLongOrNull()

                            if (sessionId != null) {
                                DoRunResult.Success(sessionId)
                            } else {
                                DoRunResult.Failure(DoRunException.DataError("400 에러: ${errorResponse.message}"))
                            }
                        } else {
                            DoRunResult.Failure(DoRunException.DataError("400 에러: 응답 본문이 없습니다."))
                        }
                    } catch (parseException: Exception) {
                        // 11. 400 에러 본문 파싱 자체를 실패 (e.g., JSON 형식이 아님)
                        DoRunResult.Failure(DoRunException.DataError("400 에러: 응답 본문 파싱 실패: ${parseException.message}"))
                    }
                } else {
                    DoRunResult.Failure(DoRunException.DataError("${e.code()} HTTP 에러: ${e.message}"))
                }
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("네트워크 요청에 실패했습니다: ${e.message}"))
            }
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

    override suspend fun saveSegmentData(
        sessionId: Long,
        isPaused: Boolean,
    ): DoRunResult<SaveRealtimeRunningDataResult.SyncResult> =
        withContext(Dispatchers.IO) {
            try {
                // 로컬 DB에서 동기화되지 않은 포인트 가져오기
                val unsyncedPoints = localRunningDataSource.getUnsyncedTrackPoints()

                // 보낼 데이터가 없는 경우, 성공으로 간주하고 일찍 반환
                if (unsyncedPoints.isEmpty()) {
                    Timber.d("saveSegmentData: No unsynced points to send.")
                    // (가정) SyncResult가 비어있을 수 있는 data class 또는 object라고 가정
                    return@withContext DoRunResult.Success(SaveRealtimeRunningDataResult.SyncResult())
                }
                Timber.d("saveSegmentData: Found ${unsyncedPoints.size} unsynced points to send.")

                // 4. 서버에 데이터 전송
                val response =
                    runningSessionDataSource.postSegmentData(
                        sessionId,
                        SaveSegmentDataRequestsDto(
                            segment = unsyncedPoints.map { it.toSegmentDataDto() },
                            isStopped = isPaused,
                        ), // Mock 데이터 대신 실제 DTO 사용
                    )

                // 서버 응답 처리
                if (response.code in 200..299) {
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
                } else {
                    // 서버 에러 처리 (실패 시, isSynced=0으로 남아있어 다음 시도 가능)
                    Timber.w("Server error on saveSegmentData: ${response.code} ${response.message}")
                    DoRunResult.Failure(DoRunException.ServerError(response.code, response.message))
                }
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
    override suspend fun finish(sessionId: Long): DoRunResult<RunningSessionResult> =
        withContext(Dispatchers.IO) {
            try {
                // 종료 직전, 5분이 안됐어도 남은 세그먼트를 모두 동기화
                Timber.d("Finishing run: Syncing final segments...")
                val segmentSyncResult = saveSegmentData(sessionId, false)
                if (segmentSyncResult is DoRunResult.Failure) {
                    // 세그먼트 마지막 동기화 실패
                    Timber.w("Failed to sync final segment before finishing.")
                    // (정책 결정 필요) 여기서 중단할지, 아니면 무시하고 종료할지.
                    // 일단 중단(Failure) 처리
                    return@withContext DoRunResult.Failure(segmentSyncResult.exception)
                }

                // 종료 API 호출
                // 종료 요청에 필요한 DTO를 생성합니다.
                // 이 DTO는 비어있거나, DB에서 요약 정보를 가져와 만들 수 있습니다.
                Timber.d("Final segments synced. Calling finish API...")
                val finishRequestDto =
                    FinishRunningRequestDto(
                        data = FinishRunningRequestData(),
                        mapImage = "TODO()",
                    )

                val response =
                    runningSessionDataSource.postFinishRunning(
                        sessionId,
                        finishRequestDto,
                    )

                // 3. 종료 응답 처리
                if (response.code in 200..299) {
                    val runningSessionResult =
                        response.data?.toRunningSessionResult()
                            ?: throw DoRunException.DataError("서버 응답 데이터(finish)가 비어 있습니다.")

                    // 4. (중요) 종료가 서버에서 성공적으로 처리되면 로컬 데이터 모두 삭제
                    localRunningDataSource.deleteAllRunningTrackPoints()
                    Timber.d("Successfully finished session. Local data cleared.")

                    DoRunResult.Success(runningSessionResult)
                } else {
                    Timber.w("Server error on finish: ${response.code} ${response.message}")
                    DoRunResult.Failure(DoRunException.ServerError(response.code, response.message))
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to finish running session")
                DoRunResult.Failure(DoRunException.DataError("러닝 종료 실패: ${e.message}"))
            }
        }
}
