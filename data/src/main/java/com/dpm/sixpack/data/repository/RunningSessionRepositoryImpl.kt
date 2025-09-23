package com.dpm.sixpack.data.repository

import com.dpm.sixpack.data.repository.di.MockRequestDataFactory
import com.dpm.sixpack.data.source.remote.datasoruce.RunningSessionDataSource
import com.dpm.sixpack.data.source.remote.dto.request.StartRunningRequestDto
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.RealtimeRunningData
import com.dpm.sixpack.domain.model.RunningSessionResult
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import com.dpm.sixpack.domain.usecase.SaveRealtimeRunningDataResult
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class RunningSessionRepositoryImpl @Inject constructor(
    private val runningSessionDataSource: RunningSessionDataSource,
    private val userPreferenceRepository: UserPreferenceRepository,
) : RunningSessionRepository {
    override suspend fun start(goalPlanId: Long): DoRunResult<Long> =
        withContext(Dispatchers.IO) {
            val localSessionId = userPreferenceRepository.getSessionId()
            if (localSessionId == null) {
                try {
                    val response =
                        runningSessionDataSource.postStartRunning(StartRunningRequestDto(goalPlanId))

                    val sessionId =
                        response.data?.sessionId
                            ?: throw DoRunException.DataError("서버 응답 데이터가 비어 있습니다.")

                    userPreferenceRepository.updateSessionId(sessionId) // localSessionId 업데이트

                    DoRunResult.Success(sessionId)
                } catch (e: Exception) {
                    DoRunResult.Failure(DoRunException.DataError("네트워크 요청에 실패했습니다: ${e.message}"))
                }
            } else {
                Timber.d("local에 sessionId가 존재합니다. localData를 호출합니다.")
                DoRunResult.Success(localSessionId)
            }
        }

    override fun getRealtimeData(): Flow<DoRunResult<RealtimeRunningData>> =
        flow {
            // 서울 시청 근처
            var lat = 37.5665
            var lon = 126.9780
            var bearingDeg = 45.0 // 진행 방향(도)
            var speed = 0.0f // m/s
            var totalDistance = 0f // m
            var durationSec = 0 // s

            val targetSpeed = 3.2f // m/s (약 5'12"/km)
            val accelPerSec = 0.25f // 웜업 가속
            val rnd = Random(System.currentTimeMillis())

            while (coroutineContext.isActive) {
                delay(1000L)

                // 1) 속도 업데이트: 웜업(+노이즈) + 클램프
                if (speed < targetSpeed) speed += accelPerSec
                speed += (rnd.nextFloat() - 0.5f) * 0.15f // ±0.075 m/s
                speed = speed.coerceIn(0f, 4.5f)

                // 2) 진행 방향 살짝 흔들림
                bearingDeg += (rnd.nextDouble() - 0.5) * 4.0 // ±2도
                val bearingRad = Math.toRadians(bearingDeg)

                // 3) 1초 이동 벡터(m)
                val dt = 1.0
                val dist = speed * dt.toFloat() // m
                val dNorth = dist * cos(bearingRad).toFloat()
                val dEast = dist * sin(bearingRad).toFloat()

                // 4) m → deg 변환
                val latRad = Math.toRadians(lat)
                val dLatDeg = dNorth / 111_320.0
                val dLonDeg = dEast / (111_320.0 * cos(latRad))

                // 5) 좌표 갱신
                lat += dLatDeg
                lon += dLonDeg

                // 6) 거리 적산(좌표 기반)
                totalDistance += dist

                // 7) 케이던스(속도 연동) - 보폭(개인 편차 + 노이즈)
                val baseStepLen = 1.0 + (rnd.nextDouble() - 0.5) * 0.2 // 0.9~1.1 m/step
                val cadence =
                    if (speed > 0.2f) {
                        val spm = (speed / baseStepLen) * 60.0
                        spm.coerceIn(140.0, 190.0).toInt() // 현실 범위
                    } else {
                        0
                    }

                // 8) 페이스(sec/km)
                val pace = if (speed > 0.3f) (1000.0 / speed).toInt() else 0

                // 9) 해발/잔노이즈
                val altitude = 25.0 + (rnd.nextDouble() - 0.5) * 0.8

                val data =
                    RealtimeRunningData(
                        latitude = lat,
                        longitude = lon,
                        altitude = altitude,
                        speed = speed, // m/s (API 보낼 땐 km/h로 변환!)
                        pace = pace, // sec/km
                        cadence = cadence, // spm
                        totalDistanceMeter = totalDistance,
                        timestamp = System.currentTimeMillis(),
                        duration = durationSec++, // 누적 러닝 시간(초)
                    )

                emit(DoRunResult.Success(data))
            }
        }

    override suspend fun saveRealtimeData(
        data: RealtimeRunningData,
    ): DoRunResult<SaveRealtimeRunningDataResult.LocalResult> {
        TODO("Not yet implemented")
    }

    override suspend fun saveSegmentData(): DoRunResult<SaveRealtimeRunningDataResult.SyncResult> =
        withContext(Dispatchers.IO) {
            val sessionId = userPreferenceRepository.getSessionId()

            if (sessionId == null) {
                return@withContext DoRunResult.Failure(
                    DoRunException.DataError("SessionRepository : 저장된 SessionId가 존재하지 않습니다."),
                )
            }

            try {
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

    override suspend fun finish(): DoRunResult<RunningSessionResult> =
        withContext(Dispatchers.IO) {
            try {
                val sessionId = userPreferenceRepository.getSessionId()

                if (sessionId == null) {
                    return@withContext DoRunResult.Failure(
                        DoRunException.DataError("SessionRepository : 저장된 SessionId가 존재하지 않습니다."),
                    )
                }
                val response =
                    runningSessionDataSource.postFinishRunning(
                        sessionId,
                        MockRequestDataFactory.createMockFinishRunningRequest(),
                    )

                val runningSessionResult =
                    response.data?.toRunningSessionResult()
                        ?: throw DoRunException.DataError("서버 응답 데이터가 비어 있습니다.")

                userPreferenceRepository.clearSessionId()
                DoRunResult.Success(runningSessionResult)
            } catch (e: Exception) {
                DoRunResult.Failure(DoRunException.DataError("네트워크 요청에 실패했습니다: ${e.message}"))
            }
        }
}
