package com.dpm.sixpack.presentation.common.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.dpm.sixpack.domain.model.RunningSessionResult
import com.dpm.sixpack.presentation.common.util.format.formatCadence
import com.dpm.sixpack.presentation.common.util.format.formatPace
import com.dpm.sixpack.presentation.common.util.format.formatSecondsToTimeInFeed
import com.dpm.sixpack.presentation.common.util.format.toPostTimeStringSafe
import com.dpm.sixpack.presentation.common.util.formatDistanceToKm
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
@Immutable
data class RunningSummary(
    val totalDistance: String = "",
    val totalTime: String = "",
    val averagePace: String = "",
    val cadence: String = "",
    val recordDateTime: String = "",
) : Parcelable

fun RunningSessionResult.toRunningSummary(postTime: String): RunningSummary =
    RunningSummary(
        totalDistance = formatDistanceToKm(totalDistanceMeter),
        totalTime = formatSecondsToTimeInFeed(totalDurationSec.toLong()),
        averagePace = formatPace(avgPace),
        cadence = formatCadence(avgCadence),
        recordDateTime = postTime.toPostTimeStringSafe(),
    )
