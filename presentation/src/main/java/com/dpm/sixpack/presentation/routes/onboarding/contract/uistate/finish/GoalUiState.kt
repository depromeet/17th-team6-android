package com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.finish

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.util.format.formatPace
import com.dpm.sixpack.presentation.common.util.format.formatSecondsToTime
import kotlinx.parcelize.Parcelize

@Parcelize
data class GoalUiState(
    val pace: Int,
    val distance: Int,
    val duration: Int,
    val roundCount: Int
) : Parcelable{
    val formattedPace: String
        get() = formatPace(pace)

    val formattedDuration: String
        get() = formatSecondsToTime(duration)
}

