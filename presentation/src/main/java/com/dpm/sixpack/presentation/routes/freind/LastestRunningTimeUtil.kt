package com.dpm.sixpack.presentation.routes.freind

import android.content.Context
import com.dpm.sixpack.core.util.TimeUtil.isoStringToEpochSeconds
import com.dpm.sixpack.presentation.R
import kotlin.math.roundToLong

/**
 * @param lastestRunAt ISO 8601 형식의 시간 문자열 (예: "2025-09-13T19:57:13Z")
 */
internal fun calculateSecDiff(lastestRunAt: String): Double? {
    val currentLocalTimeSec = System.currentTimeMillis() / 1000L

    val latestRunTimeSec: Long = isoStringToEpochSeconds(lastestRunAt) ?: return null

    return (currentLocalTimeSec - latestRunTimeSec).toDouble()
}

/**
 * "n분 전", "n시간 전" 등으로 변환
 */
internal fun convertTimeDiffToString(
    context: Context,
    secDiff: Double,
): String {
    val minuteInSec = 60
    val hourInSec = 60 * minuteInSec // 3600
    val dayInSec = 24 * hourInSec // 86400
    val twoDaysInSec = 2 * dayInSec // 172800

    return when {
        // 60초 미만 (음수 포함, 즉 미래 시간이거나 1분 미만 차이)
        secDiff < minuteInSec -> {
            val minutes = 1
            context.getString(R.string.minutes_before, minutes)
        }

        // 1시간 미만 (60초 ~ 3599초)
        secDiff < hourInSec -> {
            val minutes = (secDiff / minuteInSec).roundToLong()
            context.getString(R.string.minutes_before, minutes)
        }

        // 24시간 미만 (1시간 ~ 23시간 59분 59초)
        secDiff < dayInSec -> {
            val hours = (secDiff / hourInSec).roundToLong()
            context.getString(R.string.hours_before, hours)
        }

        // 24시간 이상
        else -> {
            val days = (secDiff / dayInSec).roundToLong()
            context.getString(R.string.days_before, days)
        }
    }
}
