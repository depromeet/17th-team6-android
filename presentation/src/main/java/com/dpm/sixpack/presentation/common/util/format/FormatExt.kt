package com.dpm.sixpack.presentation.common.util.format

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

private val KOREAN_FEED_TIME_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy.MM.dd·a h:mm", Locale.KOREAN)
private val ASIA_ZONE_ID = ZoneId.of("Asia/Seoul")

fun String.toKoreanFeedTimeStringOrNull(): String? {
    return runCatching {
        Instant
            .parse(this)
            .atZone(ASIA_ZONE_ID)
            .format(KOREAN_FEED_TIME_FORMATTER)
    }.getOrNull() // 4. 파싱/변환 실패 시 null 반환
}
