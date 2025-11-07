package com.dpm.sixpack.presentation.common.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.dpm.sixpack.domain.model.RunSession
import com.dpm.sixpack.presentation.common.util.format.formatCadence
import com.dpm.sixpack.presentation.common.util.format.formatPace
import com.dpm.sixpack.presentation.common.util.format.formatSecondsToTimeInFeed
import com.dpm.sixpack.presentation.common.util.format.toDateWithDayOfWeekOrNull
import com.dpm.sixpack.presentation.common.util.format.toPostTimeStringOrNull
import com.dpm.sixpack.presentation.common.util.format.toTimeOnlyOrNull
import com.dpm.sixpack.presentation.common.util.formatDistanceToKm
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

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
        get() = postTime.toTimeOnlyOrNull() ?: LocalDate.now().toString()
    val formattedDate: String
        get() = postTime.toDateWithDayOfWeekOrNull() ?: LocalDate.now().toString()
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
                recordDateTime = finishedAt.toPostTimeStringOrNull() ?: LocalDate.now().toString(),
            ),
        mapImageUrl = mapImage,
        isPosted = isSelfied,
        postTime = finishedAt,
    )

fun List<RunSession>.toRecordItems(): List<RecordItem> = map { it.toRecordItem() }
