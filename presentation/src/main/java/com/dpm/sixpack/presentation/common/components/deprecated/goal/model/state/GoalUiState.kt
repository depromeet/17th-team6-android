package com.dpm.sixpack.presentation.common.components.deprecated.goal.model.state

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.components.deprecated.goal.model.type.MarathonGoalType
import com.dpm.sixpack.presentation.common.util.format.formatPace
import com.dpm.sixpack.presentation.common.util.format.formatSecondsToTime
import kotlinx.parcelize.Parcelize

@Parcelize
data class GoalUiState(
    val pace: Int,
    val distance: Int,
    val duration: Int,
    val roundCount: Int,
) : Parcelable {
    val formattedPace: String
        get() = formatPace(pace)

    val formattedDuration: String
        get() = formatSecondsToTime(duration)

    val formattedRoundCount: String
        get() = "$roundCount"

    val marathonImgRes: Int
        get() = MarathonGoalType.entries.find { it.distance == distance }?.img ?: MarathonGoalType.TEN.img
}
