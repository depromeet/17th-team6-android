package com.dpm.sixpack.presentation.common.util.format

import timber.log.Timber
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

fun formatSecondsToTime(totalSeconds: Int): String {
    val seconds = abs(totalSeconds)
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}

fun formatPace(paceInSeconds: Int): String {
    val minutes = paceInSeconds / 60
    val seconds = paceInSeconds % 60

    return String.format("%d'%02d\"", minutes, seconds)
}

fun formatCadence(cadence: Int) = "$cadence spm"

fun formatSecondsToTimeInFeed(totalSeconds: Long): String {
    val seconds = abs(totalSeconds)
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, remainingSeconds)
    } else {
        String.format("%02d:%02d", minutes, remainingSeconds)
    }
}

fun formatPace(paceInSeconds: Long): String {
    val minutes = paceInSeconds / 60
    val seconds = paceInSeconds % 60

    return String.format("%d'%02d\"", minutes, seconds)
}

private val POST_TIME_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy.MM.dd '·' a h:mm", Locale.KOREAN)
private val DATE_WITH_DAY_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy.MM.dd (E)", Locale.KOREAN)
private val AM_PM_TIME_FORMATTER =
    DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN)
private val ASIA_ZONE_ID = ZoneId.of("Asia/Seoul")

private val DEFAULT_FAILED = "알 수 없음"

/**
 * 문자열을 KST 시간대 ZonedDateTime으로 변환 (실패 시 null)
 * 1. Instant.parse (UTC, 오프셋이 있는 '...Z' 형식) 시도
 * 2. LocalDateTime.parse (오프셋이 없는 로컬 형식, UTC로 간주) 시도
 */
private fun String.toKstZonedDateTimeOrNull(): ZonedDateTime? {
    // 1. Instant.parse (예: "2025-11-13T12:00:00Z") 시도
    val instantResult =
        runCatching {
            Instant.parse(this).atZone(ASIA_ZONE_ID)
        }
    if (instantResult.isSuccess) {
        return instantResult.getOrNull() // 성공 시 KST ZonedDateTime 반환
    }

    // 2. LocalDateTime.parse (예: "2025-11-13T12:00:00") 시도
    val localResult =
        runCatching {
            LocalDateTime
                .parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .atZone(ZoneId.of("UTC")) // UTC 시간으로 간주
                .withZoneSameInstant(ASIA_ZONE_ID) // KST로 변환
        }
    if (localResult.isSuccess) {
        return localResult.getOrNull() // 성공 시 KST ZonedDateTime 반환
    }

    Timber.e("'$this' 파싱 실패! Instant/LocalDateTime 형식 모두 아님. (에러: ${instantResult.exceptionOrNull()?.message})")
    return null
}

// --- 2. 공개 함수 (Non-Nullable, 실패 시 "?" 반환) ---

/**
 * [안전한 변환]
 * "yyyy.MM.dd · a h:mm" 형식으로 변환 (실패 시 "?")
 * (예: "2025.11.13 · 오후 9:15")
 */
fun String.toPostTimeStringSafe(): String {
    return toKstZonedDateTimeOrNull()
        ?.format(POST_TIME_FORMATTER)
        ?: DEFAULT_FAILED // 실패 시 기본값
}

/**
 * [안전한 변환]
 * "yyyy.MM.dd (E)" 형식으로 변환 (실패 시 "?")
 * (예: "2025.11.13 (목)")
 */
fun String.toDateWithDayOfWeekSafe(): String {
    return toKstZonedDateTimeOrNull()
        ?.format(DATE_WITH_DAY_FORMATTER)
        ?: DEFAULT_FAILED // 실패 시 기본값
}

/**
 * [안전한 변환]
 * "a h:mm" 형식으로 변환 (실패 시 "?")
 * (예: "오후 9:15")
 */
fun String.toTimeOnlySafe(): String {
    return toKstZonedDateTimeOrNull()
        ?.format(AM_PM_TIME_FORMATTER)
        ?: DEFAULT_FAILED // 실패 시 기본값
}
