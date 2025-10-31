package com.dpm.sixpack.presentation.common.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.dpm.sixpack.domain.model.RunningSessionResult
import com.dpm.sixpack.presentation.common.util.format.formatPace
import com.dpm.sixpack.presentation.common.util.format.formatSecondsToTime
import com.dpm.sixpack.presentation.common.util.format.toKoreanFeedTimeStringOrNull
import com.dpm.sixpack.presentation.common.util.formatDistanceToKm
import com.dpm.sixpack.presentation.common.util.formatPaceToString
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class RunningSummary(
    val totalDistance: String = "",
    val totalTime: String = "",
    val averagePace: String = "",
    val cadence: String = "",
    val recordDateTime: String = "",
) : Parcelable

fun RunningSessionResult.toRunningSummary(postTime: String): RunningSummary {
    return RunningSummary(
        totalDistance = formatDistanceToKm(totalDistanceMeter),
        totalTime = formatSecondsToTime(totalDurationSec),
        averagePace = formatPace(avgPace),
        cadence = avgCadence.toString(),
        recordDateTime = postTime.toKoreanFeedTimeStringOrNull() ?: "날짜를 알 수 없음"
    )
}
