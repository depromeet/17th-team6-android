package com.dpm.sixpack.presentation.routes.session_list.contract

import android.os.Parcelable
import androidx.annotation.DrawableRes
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
data class SessionListScreenState(
    val loading: Boolean = true,
    val totalGoalComponentState: SessionListTotalGoalComponentState = SessionListTotalGoalComponentState(),
    val sessionList: List<SessionListItemState> = emptyList(),
) : UiState, Parcelable

@Parcelize
data class SessionListTotalGoalComponentState(
    @DrawableRes val imageRes: Int? = null,
    val title: String = "",
    private val totalSessionCount: Int = 0,
    private val completedSessionCount: Int = 0
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

fun RunningTotalGoal.asUiState() = SessionListTotalGoalComponentState(
    title = title,
    // TODO SR-N 프리런칭 때는 마라톤만 고려. ype enum 적용하고, 마라톤 외에도 구현.
    imageRes = when {
        distance < 10000 -> R.drawable.ill_marathon_10km
        distance < 21000 -> R.drawable.ill_marathon_21km
        else -> R.drawable.ill_marathon_42km
    },
    totalSessionCount = totalRoundCount,
    completedSessionCount = clearedRoundCount,
)

@Parcelize
data class SessionListItemState(
    val id: Long,
    val roundCount: Int,
    val distance: String,
    val duration: String,
    val pace: String,
    val isCompleted: Boolean,
    val isSelected: Boolean = false
) : Parcelable {
    val showButton by lazy { isCompleted && isSelected }
}

fun RunningSessionGoal.asUiState() = SessionListItemState(
    id = id,
    roundCount = roundCount,
    distance = formatDistanceToKm(distance),
    duration = formatSecondsToTime(duration),
    pace = formatSecondsToPace(pace),
    isCompleted = clearedAt != null,
    isSelected = false,
)

