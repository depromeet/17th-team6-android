package com.dpm.sixpack.core.util

import timber.log.Timber
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

object TimeUtil {
    /**
     * Convert ms to "HH:mm:ss" or "mm:ss"
     */
    fun formatMillisWithDuration(millis: Long): String {
        val safeMillis = if (millis < 0) 0 else millis

        val duration = Duration.ofMillis(safeMillis)

        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60

        return if (hours > 0) {
            val paddedMinutes = minutes.toString().padStart(2, '0')
            val paddedSeconds = seconds.toString().padStart(2, '0')
            "$hours:$paddedMinutes:$paddedSeconds"
        } else {
            val totalMinutes = duration.toMinutes()
            val paddedSeconds = seconds.toString().padStart(2, '0')
            "$totalMinutes:$paddedSeconds"
        }
    }

    /**
     * ISO 8601 형식의 시간 문자열 (예: "2025-09-13T19:57:13Z") -> Epoch 초
     *
     * 두 가지 형식을 모두 처리합니다:
     * 1. "2025-09-13T19:57:13Z" (시간대 'Z' 포함)
     * 2. "2025-11-05T18:09:06.187261" (시간대 정보 없음)
     */
    fun isoStringToEpochSeconds(timestamp: String): Long? {
        try {
            // 1. [기존 방식] 'Z' 또는 Offset이 포함된 표준 Instant 문자열 파싱 시도
            // (예: "2025-09-13T19:57:13Z")
            val instant = Instant.parse(timestamp)
            return instant.epochSecond
        } catch (e: DateTimeParseException) {
            // 2. [새 방식] 'Z'가 없어서 실패한 경우, LocalDateTime으로 다시 파싱 시도
            // (예: "2025-11-05T18:09:06.187261")
            try {
                val localDateTime = LocalDateTime.parse(timestamp)

                // 3. (중요) Offset이 없었으므로, 이 시간이 UTC 기준이라고 *가정*하고 Epoch 초로 변환
                //    ✅ [수정] .toInstant().epochSecond 대신 .toEpochSecond()를 직접 사용
                return localDateTime.toEpochSecond(ZoneOffset.UTC)
            } catch (e2: DateTimeParseException) {
                // 두 가지 방식 모두 실패한 경우
                Timber.e(e2, "Error parsing timestamp (in both formats): $timestamp")
                e2.printStackTrace()
                return null
            }
        }
    }

    /*
     * Epoch 밀리초 -> ISO 8601 형식의 시간 문자열 (예: "2025-09-13T19:57:13Z")
     */
    fun formatMillisToIsoUtc(timestamp: Long): String =
        Instant.ofEpochMilli(timestamp).truncatedTo(ChronoUnit.SECONDS).toString()

    /**
     * 다양한 형식의 날짜 문자열을 LocalDateTime으로 파싱합니다.
     *
     * 지원하는 형식:
     * 1. ISO_DATE_TIME: "2025-11-09T10:30:00" (시간 포함)
     * 2. ISO_LOCAL_DATE: "2025-11-09" (날짜만)
     *
     * @param dateString 파싱할 날짜 문자열
     * @return 파싱된 LocalDateTime 객체, 파싱 실패 시 null
     */
    fun parseToLocalDateTime(dateString: String): LocalDateTime? {
        return try {
            // 1. ISO_DATE_TIME 형식 시도 (예: "2025-11-09T10:30:00")
            try {
                LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
            } catch (_: DateTimeParseException) {
                // 2. ISO_LOCAL_DATE 형식 시도 (예: "2025-11-09")
                LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
                    .atStartOfDay()
            }
        } catch (e: DateTimeParseException) {
            Timber.e(e, "날짜 파싱 실패: $dateString")
            null
        }
    }
}
