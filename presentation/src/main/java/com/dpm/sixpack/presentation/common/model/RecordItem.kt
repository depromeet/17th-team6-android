package com.dpm.sixpack.presentation.common.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.dpm.sixpack.domain.model.RunSession
import com.dpm.sixpack.presentation.common.util.format.formatCadence
import com.dpm.sixpack.presentation.common.util.format.formatPace
import com.dpm.sixpack.presentation.common.util.format.formatSecondsToTimeInFeed
import com.dpm.sixpack.presentation.common.util.format.toDateWithDayOfWeekSafe
import com.dpm.sixpack.presentation.common.util.format.toPostTimeStringSafe
import com.dpm.sixpack.presentation.common.util.format.toTimeOnlySafe
import com.dpm.sixpack.presentation.common.util.formatDistanceToKm
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class RecordItem(
    val sessionId: Long = 0,
    val runningSummary: RunningSummary = RunningSummary(),
    val mapImageUrl: String = "",
    val isPosted: Boolean = false,
    val postTime: String = "",
) : Parcelable {
    val formattedTime: String
        get() = postTime.toTimeOnlySafe()
    val formattedDate: String
        get() = postTime.toDateWithDayOfWeekSafe()
}

fun RunSession.toRecordItem(): RecordItem =
    RecordItem(
        sessionId = runSessionId,
        runningSummary =
            RunningSummary(
                totalDistance = formatDistanceToKm(distanceTotal),
                totalTime = formatSecondsToTimeInFeed(durationTotal.toLong()),
                averagePace = formatPace(paceAvg),
                cadence = formatCadence(cadenceAvg),
                recordDateTime = createdAt.toPostTimeStringSafe(),
            ),
        mapImageUrl = mapImage,
        isPosted = isSelfied,
        postTime = createdAt,
    )

fun List<RunSession>.toRecordItems(): List<RecordItem> = map { it.toRecordItem() }
