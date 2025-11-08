package com.dpm.sixpack.data.source.remote.service

import com.dpm.sixpack.data.source.remote.dto.request.SaveSegmentDataRequestsDto
import com.dpm.sixpack.data.source.remote.dto.response.FinishRunningResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.RunSessionListResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.SaveSegmentResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.SessionDetailResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.StartRunningResponseDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * RunningSessionServiceApi의 Mock 구현체
 * 실제 API 호출 없이 테스트 및 개발을 위한 Mock 데이터를 반환합니다.
 */
class MockRunningSessionServiceApi
    @Inject
    constructor() : RunningSessionServiceApi {
        /**
         * 현재 타임스탬프를 ISO_DATE_TIME 형식으로 반환 (타임존 정보 포함)
         */
        private fun getCurrentTimestamp(): String =
            ZonedDateTime
                .now(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_DATE_TIME)

        override suspend fun postStartSession(): BaseResponse<StartRunningResponseDto> {
            delay(300L) // 네트워크 지연 시뮬레이션
            return BaseResponse(
                status = "200",
                message = "러닝 세션 시작 성공",
                timestamp = getCurrentTimestamp(),
                data =
                    StartRunningResponseDto(
                        sessionId = System.currentTimeMillis(), // 고유한 세션 ID 생성
                    ),
            )
        }

        override suspend fun postFinishRunning(
            sessionId: Long,
            data: RequestBody,
            mapImage: MultipartBody.Part,
        ): BaseResponse<FinishRunningResponseDto> {
            delay(500L) // 파일 업로드 시뮬레이션을 위해 더 긴 지연

            val currentTime = getCurrentTimestamp()
            val distanceTotal = (5000..15000).random() // 5km ~ 15km
            val durationTotal = (1800..5400).random() // 30분 ~ 90분
            val paceAvg = if (distanceTotal > 0) (durationTotal * 1000) / distanceTotal else 360 // 초/km
            val paceMax = (paceAvg * 0.7).toInt() // 평균 페이스보다 빠른 최대 페이스

            return BaseResponse(
                status = "200",
                message = "러닝 종료 성공",
                timestamp = currentTime,
                data =
                    FinishRunningResponseDto(
                        id = sessionId,
                        createdAt = currentTime,
                        updatedAt = currentTime,
                        finishedAt = currentTime,
                        distanceTotal = distanceTotal,
                        durationTotal = durationTotal,
                        paceAvg = paceAvg,
                        paceMax = paceMax,
                        paceMaxLatitude = 37.5665 + (Math.random() * 0.01 - 0.005), // 서울 근처 랜덤 좌표
                        paceMaxLongitude = 126.9780 + (Math.random() * 0.01 - 0.005),
                        cadenceAvg = (150..180).random(),
                        cadenceMax = (180..200).random(),
                        mapImage = "https://picsum.photos/800/600?random=$sessionId",
                    ),
            )
        }

        override suspend fun postSegmentData(
            sessionId: Long,
            saveSegmentDataRequestsDto: SaveSegmentDataRequestsDto,
        ): BaseResponse<SaveSegmentResponseDto> {
            delay(200L)
            return BaseResponse(
                status = "200",
                message = "세그먼트 데이터 저장 성공",
                timestamp = getCurrentTimestamp(),
                data = null, // SaveSegmentResponseDto가 null일 수 있음
            )
        }

        override suspend fun getRunSessions(
            isSelfied: Boolean?,
            startDateTime: String?,
        ): BaseResponse<List<RunSessionListResponseDto>> {
            delay(500L) // 네트워크 지연 시뮬레이션

            // startDateTime을 파싱하여 해당 월의 데이터 생성
            val targetMonth =
                startDateTime?.let {
                    try {
                        ZonedDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME).monthValue
                    } catch (e: Exception) {
                        ZonedDateTime.now(ZoneId.systemDefault()).monthValue
                    }
                } ?: ZonedDateTime.now(ZoneId.systemDefault()).monthValue

            val targetYear =
                startDateTime?.let {
                    try {
                        ZonedDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME).year
                    } catch (e: Exception) {
                        ZonedDateTime.now(ZoneId.systemDefault()).year
                    }
                } ?: ZonedDateTime.now(ZoneId.systemDefault()).year

            // Mock 세션 데이터 생성
            val mockSessions = generateMockSessions(targetYear, targetMonth, isSelfied)

            return BaseResponse(
                status = "200",
                message = "러닝 기록 조회 성공",
                timestamp = getCurrentTimestamp(),
                data = mockSessions,
            )
        }

        override suspend fun getSessionDetail(sessionId: Long): BaseResponse<SessionDetailResponseDto> {
            TODO("Not yet implemented")
        }

        /**
         * Mock 러닝 세션 데이터 생성
         */
        private fun generateMockSessions(
            year: Int,
            month: Int,
            isSelfied: Boolean?,
        ): List<RunSessionListResponseDto> {
            val sessions = mutableListOf<RunSessionListResponseDto>()
            val daysInMonth = getDaysInMonth(year, month)

            // 5-10개의 랜덤 세션 생성
            val sessionCount = (5..10).random()
            val usedDays = mutableSetOf<Int>()

            repeat(sessionCount) { index ->
                // 중복되지 않는 랜덤 날짜 선택
                var day: Int
                do {
                    day = (1..daysInMonth).random()
                } while (usedDays.contains(day))
                usedDays.add(day)

                val hour = (6..20).random()
                val minute = (0..59).random()

                val createdAt =
                    ZonedDateTime
                        .of(year, month, day, hour, minute, 0, 0, ZoneId.systemDefault())
                        .format(DateTimeFormatter.ISO_DATE_TIME)

                val finishedAt =
                    ZonedDateTime
                        .of(year, month, day, hour, minute, 0, 0, ZoneId.systemDefault())
                        .plusMinutes((30..90).random().toLong())
                        .format(DateTimeFormatter.ISO_DATE_TIME)

                val distanceTotal = (3000..15000).random() // 3km ~ 15km (미터)
                val durationTotal = (1800..5400).random() // 30분 ~ 90분 (초)
                val paceAvg = if (distanceTotal > 0) (durationTotal * 1000) / distanceTotal else 360 // 초/km
                val cadenceAvg = (150..180).random() // 분당 걸음 수
                val selfieStatus =
                    when {
                        isSelfied == true -> true
                        isSelfied == false -> false
                        else -> index % 3 == 0 // 셀피 있는 세션과 없는 세션 혼합
                    }

                sessions.add(
                    RunSessionListResponseDto(
                        runSessionId = (index + 1).toLong(),
                        createdAt = createdAt,
                        updatedAt = createdAt,
                        finishedAt = finishedAt,
                        distanceTotal = distanceTotal,
                        durationTotal = durationTotal,
                        paceAvg = paceAvg,
                        cadenceAvg = cadenceAvg,
                        isSelfied = selfieStatus,
                        mapImage = "https://picsum.photos/400/300?random=$index",
                    ),
                )
            }

            // finishedAt 기준 내림차순 정렬 (최신순)
            return sessions.sortedByDescending { it.finishedAt }
        }

        /**
         * 해당 월의 일수 계산
         */
        private fun getDaysInMonth(
            year: Int,
            month: Int,
        ): Int =
            when (month) {
                1, 3, 5, 7, 8, 10, 12 -> 31
                4, 6, 9, 11 -> 30
                2 -> if (isLeapYear(year)) 29 else 28
                else -> 30
            }

        /**
         * 윤년 확인
         */
        private fun isLeapYear(year: Int): Boolean = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
