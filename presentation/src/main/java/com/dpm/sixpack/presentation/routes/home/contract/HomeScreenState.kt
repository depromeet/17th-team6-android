package com.dpm.sixpack.presentation.routes.home.contract

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dpm.sixpack.domain.model.session.RunningSessionGoal
import com.dpm.sixpack.domain.model.total.RunningTotalGoal
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.common.util.formatDistanceToKm
import com.dpm.sixpack.presentation.common.util.formatSecondsToPace
import com.dpm.sixpack.presentation.common.util.formatSecondsToTime
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeScreenState(
    val loading: Boolean = true,
    val totalGoalComponentState: HomeTotalGoalComponentState = HomeTotalGoalComponentState(),
    val sessionComponentState: HomeSessionComponentState = HomeSessionComponentState(),
    val totalGoalCompleted: Boolean = false,
) : UiState,
    Parcelable

@Parcelize
data class HomeTotalGoalComponentState(
    @DrawableRes val imageRes: Int? = null, // TODO enum 으로 변경?
    val title: String = "",
    val distance: String = "",
    val duration: String = "",
    val pace: String = "",
    private val totalSessionCount: Int = 0,
    private val completedSessionCount: Int = 0,
) : Parcelable {
    @IgnoredOnParcel
    val safeTotalSessionCount by lazy {
        totalSessionCount.coerceAtLeast(0)
    }

    @IgnoredOnParcel
    val safeCurrentSessionCount by lazy {
        completedSessionCount.coerceIn(
            0,
            if (safeTotalSessionCount == 0) 0 else safeTotalSessionCount,
        )
    }

    @IgnoredOnParcel
    val sessionProgress by lazy {
        if (safeTotalSessionCount > 0) {
            safeCurrentSessionCount.toFloat() / safeTotalSessionCount
        } else {
            0f
        }
    }
}

fun RunningTotalGoal.asUiState() =
    HomeTotalGoalComponentState(
        // TODO SR-N 프리런칭 때는 마라톤만 고려. ype enum 적용하고, 마라톤 외에도 구현.
        imageRes =
            when {
                distance < 10000 -> R.drawable.ill_marathon_10km
                distance < 21000 -> R.drawable.ill_marathon_21km
                else -> R.drawable.ill_marathon_42km
            },
        title = title,
        distance = formatDistanceToKm(distance),
        duration = formatSecondsToTime(duration),
        pace = formatSecondsToPace(pace),
        totalSessionCount = totalRoundCount,
        completedSessionCount = clearedRoundCount,
    )

@Parcelize
data class HomeSessionComponentState(
    val sessionCount: Int? = null,
    @StringRes val cheerUpStringRes: Int? = null, // TODO enum 으로 변경?
    val distance: String = "",
    val duration: String = "",
    val pace: String = "",
) : Parcelable {
    val showPreviousSession: Boolean
        get() = (sessionCount ?: 0) > 1
}

fun RunningSessionGoal.asUiState(totalRoundCount: Int): HomeSessionComponentState {
    val roundProgress = roundCount / totalRoundCount.toFloat()
    return HomeSessionComponentState(
        sessionCount = roundCount,
        cheerUpStringRes =
            when {
                roundProgress <= 0.01f -> R.string.home_goal_cheer_up_0_1
                roundProgress <= 0.25f -> R.string.home_goal_cheer_up_1_25
                roundProgress <= 0.5f -> R.string.home_goal_cheer_up_26_50
                roundProgress <= 0.75 -> R.string.home_goal_cheer_up_51_75
                else -> R.string.home_goal_cheer_up_76_100
            },
        distance = formatDistanceToKm(distance),
        duration = formatSecondsToTime(duration),
        pace = formatSecondsToPace(pace),
    )
}
