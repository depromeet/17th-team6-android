package com.dpm.sixpack.presentation.routes.freind

import com.dpm.sixpack.core.util.TimeUtil.isoStringToEpochSeconds
import kotlin.math.roundToLong


/**
 * ISO 8601 시간 문자열을 받아 "n분 전", "n시간 전" 등으로 변환
 *
 * @param latestRunAt ISO 8601 형식의 시간 문자열 (예: "2025-09-13T19:57:13Z")
 * @return 48시간 초과 시 null, 그 외에는 변환된 문자열
 */
internal fun calculateLatestRunTime(latestRunAt: String): String? {
    val currentLocalTimeSec = System.currentTimeMillis() / 1000L

    val latestRunTimeSec: Long = isoStringToEpochSeconds(latestRunAt) ?: return null

    val diffSec = (currentLocalTimeSec - latestRunTimeSec).toDouble()

    val minuteInSec = 60
    val hourInSec = 60 * minuteInSec // 3600
    val dayInSec = 24 * hourInSec // 86400
    val twoDaysInSec = 2 * dayInSec // 172800

    return when {
        // 60초 미만 (음수 포함, 즉 미래 시간이거나 1분 미만 차이)
        diffSec < minuteInSec -> "1분 전"

        // 1시간 미만 (60초 ~ 3599초)
        diffSec < hourInSec -> {
            val minutes = (diffSec / minuteInSec).roundToLong()
            "${minutes}분 전"
        }

        // 24시간 미만 (1시간 ~ 23시간 59분 59초)
        diffSec < dayInSec -> {
            val hours = (diffSec / hourInSec).roundToLong()
            "${hours}시간 전"
        }

        // 24시간 이상
        else  -> {
            val days = (diffSec / dayInSec).roundToLong()
            "${days}일 전"
        }
    }
}
