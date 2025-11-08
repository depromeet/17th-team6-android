package com.dpm.sixpack.presentation.common.util.format

import timber.log.Timber
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
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

fun String.toPostTimeStringOrNull(): String? =
    runCatching {
        val localDateTime = LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        localDateTime
            .atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(ASIA_ZONE_ID)
            .format(POST_TIME_FORMATTER)
    }.onFailure { error ->
        Timber.e("파싱 실패! 에러: ${error.message}")
    }.getOrNull()

fun String.toPostTimeStringOrNullInstant(): String? =
    runCatching {
        // 1. LocalDateTime.parse 대신 Instant.parse 사용
        val instant = Instant.parse(this)

        // 2. atZone(ASIA_ZONE_ID)으로 바로 변환
        instant
            .atZone(ASIA_ZONE_ID) // 'atZone(UTC)' 및 'withZoneSameInstant' 불필요
            .format(POST_TIME_FORMATTER)
    }.onFailure { error ->
        Timber.e("파싱 실패! 에러: ${error.message}")
    }.getOrNull()

fun String.toDateWithDayOfWeekOrNull(): String? =
    runCatching {
        val localDateTime = LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        localDateTime
            .atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(ASIA_ZONE_ID)
            .format(AM_PM_TIME_FORMATTER)
    }.onFailure { error ->
        Timber.e("날짜 파싱 실패! 에러: ${error.message}")
    }.getOrNull()

fun String.toTimeOnlyOrNull(): String? =
    runCatching {
        val localDateTime = LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        localDateTime
            .atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(ASIA_ZONE_ID)
            .format(AM_PM_TIME_FORMATTER)
    }.onFailure { error ->
        Timber.e("시간 파싱 실패! 에러: ${error.message}")
    }.getOrNull()
