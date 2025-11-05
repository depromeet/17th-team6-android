package com.dpm.sixpack.data.source.remote.service

import com.dpm.sixpack.data.source.remote.dto.request.FinishRunningRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.SaveSegmentDataRequestsDto
import com.dpm.sixpack.data.source.remote.dto.request.StartRunningRequestDto
import com.dpm.sixpack.data.source.remote.dto.response.FinishRunningResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.RunSessionListResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.SaveSegmentResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.StartRunningResponseDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Mock implementation of RunningSessionService for testing and development.
 * Returns mock running session data.
 */
class MockRunningSessionService
    @Inject
    constructor() : RunningSessionService {
        private fun getCurrentTimestamp(): String =
            LocalDateTime
                .now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        override suspend fun postFinishRunning(
            sessionId: Long,
            finishRunningRequestDto: FinishRunningRequestDto,
        ): BaseResponse<FinishRunningResponseDto> {
            delay(300L)
            // Return empty response - not implemented yet
            return BaseResponse(
                status = "200",
                message = "러닝 종료 성공",
                timestamp = getCurrentTimestamp(),
                data = null,
            )
        }

        override suspend fun postSegmentData(
            sessionId: Long,
            saveSegmentDataRequestsDto: SaveSegmentDataRequestsDto,
        ): BaseResponse<SaveSegmentResponseDto> {
            delay(300L)
            // Return empty response - not implemented yet
            return BaseResponse(
                status = "200",
                message = "세그먼트 저장 성공",
                timestamp = getCurrentTimestamp(),
                data = null,
            )
        }

        override suspend fun postStartRunning(
            startRunningRequestDto: StartRunningRequestDto,
        ): BaseResponse<StartRunningResponseDto> {
            delay(300L)
            // Return empty response - not implemented yet
            return BaseResponse(
                status = "200",
                message = "러닝 시작 성공",
                timestamp = getCurrentTimestamp(),
                data = null,
            )
        }

        override suspend fun getRunSessions(
            isSelfied: Boolean?,
            startDateTime: String?,
        ): BaseResponse<List<RunSessionListResponseDto>> {
            delay(500L) // Simulate network delay

            // Parse startDateTime to determine which month's data to return
            val targetMonth =
                startDateTime?.let {
                    try {
                        LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME).monthValue
                    } catch (e: Exception) {
                        LocalDateTime.now().monthValue
                    }
                } ?: LocalDateTime.now().monthValue

            val targetYear =
                startDateTime?.let {
                    try {
                        LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME).year
                    } catch (e: Exception) {
                        LocalDateTime.now().year
                    }
                } ?: LocalDateTime.now().year

            // Generate mock data for the requested month
            val mockSessions = generateMockSessions(targetYear, targetMonth, isSelfied)

            return BaseResponse(
                status = "200",
                message = "러닝 기록 조회 성공",
                timestamp = getCurrentTimestamp(),
                data = mockSessions,
            )
        }

        private fun generateMockSessions(
            year: Int,
            month: Int,
            isSelfied: Boolean?,
        ): List<RunSessionListResponseDto> {
            val sessions = mutableListOf<RunSessionListResponseDto>()
            val daysInMonth = getDaysInMonth(year, month)

            // Generate 5-10 random sessions for the month
            val sessionCount = (5..10).random()
            val usedDays = mutableSetOf<Int>()

            repeat(sessionCount) { index ->
                // Pick a random day that hasn't been used
                var day: Int
                do {
                    day = (1..daysInMonth).random()
                } while (usedDays.contains(day))
                usedDays.add(day)

                val hour = (6..20).random()
                val minute = (0..59).random()

                val createdAt =
                    LocalDateTime
                        .of(year, month, day, hour, minute)
                        .atZone(java.time.ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_DATE_TIME)

                val finishedAt =
                    LocalDateTime
                        .of(year, month, day, hour, minute)
                        .plusMinutes((30..90).random().toLong())
                        .atZone(java.time.ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_DATE_TIME)

                val distanceTotal = (3000..15000).random() // 3km ~ 15km in meters
                val durationTotal = (1800..5400).random() // 30min ~ 90min in seconds
                val paceAvg = if (distanceTotal > 0) (durationTotal * 1000) / distanceTotal else 360 // seconds per km
                val cadenceAvg = (150..180).random() // steps per minute
                val selfieStatus =
                    when {
                        isSelfied == true -> true
                        isSelfied == false -> false
                        else -> index % 3 == 0 // Mix of selfied and non-selfied
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

            // Sort by finishedAt descending (most recent first)
            return sessions.sortedByDescending { it.finishedAt }
        }

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

        private fun isLeapYear(year: Int): Boolean = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
