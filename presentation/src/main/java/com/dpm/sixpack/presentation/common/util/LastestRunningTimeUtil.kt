package com.dpm.sixpack.presentation.common.util

import android.content.Context
import com.dpm.sixpack.presentation.R
import java.time.Duration
import java.time.Instant
import kotlin.math.roundToLong

/**
 * "n분 전", "n시간 전" 등으로 변환
 */
internal fun convertTimeDiffToString(
    context: Context,
    secDiff: Long,
): String {
    val minuteInSec = 60
    val hourInSec = 60 * minuteInSec // 3600
    val dayInSec = 24 * hourInSec // 86400
    val yearInSec = 365 * dayInSec // 31536000

    return when {
        // 60초 미만 (음수 포함, 즉 미래 시간이거나 1분 미만 차이)
        secDiff < minuteInSec -> {
            val minutes = 1
            context.getString(R.string.minutes_before, minutes)
        }

        // 1시간 미만 (60초 ~ 3599초)
        secDiff < hourInSec -> {
            val minutes = (secDiff / minuteInSec)
            context.getString(R.string.minutes_before, minutes)
        }

        // 24시간 미만 (1시간 ~ 23시간 59분 59초)
        secDiff < dayInSec -> {
            val hours = (secDiff / hourInSec)
            context.getString(R.string.hours_before, hours)
        }

        // 24시간 이상 1년 미만
        secDiff < yearInSec -> {
            val days = (secDiff / dayInSec)
            context.getString(R.string.days_before, days)
        }

        // 1년 이상
        else -> {
            val years = (secDiff / yearInSec)
            context.getString(R.string.days_before, years)
        }
    }
}

// TODO 승규형 이거 UTC 로 비교해야 잘된데! 바꾼거 확인부탁!
fun String.toTimeAgoString(context: Context): String {
    val postInstant = runCatching { Instant.parse(this) }.getOrNull() ?: return ""

    val nowInstant = Instant.now()

    val secDiff = Duration.between(postInstant, nowInstant).seconds

    // 4. 상수 정의 (Long 타입으로)
    val minuteInSec = 60L
    val hourInSec = 3600L
    val dayInSec = 86400L

    return when {
        // 5. 🌟 (버그 수정) 미래 시간이거나 1분 미만 차이일 때
        secDiff < minuteInSec -> {
            context.getString(R.string.minutes_before, 1)
        }

        secDiff < hourInSec -> {
            val minutes = (secDiff / minuteInSec)
            context.getString(R.string.minutes_before, minutes)
        }

        secDiff < dayInSec -> {
            val hours = (secDiff / hourInSec)
            context.getString(R.string.hours_before, hours)
        }

        else -> {
            val days = (secDiff / dayInSec)
            context.getString(R.string.days_before, days)
        }
    }
}
