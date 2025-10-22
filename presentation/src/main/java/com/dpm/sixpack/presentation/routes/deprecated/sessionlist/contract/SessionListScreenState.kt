package com.dpm.sixpack.presentation.routes.deprecated.sessionlist.contract

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class SessionListScreenState(
    val loading: Boolean = true,
    val totalGoalComponentState: SessionListTotalGoalComponentState = SessionListTotalGoalComponentState(),
    val sessionList: List<SessionListItemState> = emptyList(),
) : UiState,
    Parcelable

@Parcelize
data class SessionListTotalGoalComponentState(
    @DrawableRes val imageRes: Int? = null,
    val title: String = "",
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

@Parcelize
data class SessionListItemState(
    val id: Long,
    val roundCount: Int,
    val distance: String,
    val duration: String,
    val pace: String,
    val isCompleted: Boolean,
    val isSelected: Boolean = false,
) : Parcelable {
    val showButton by lazy { isCompleted && isSelected }
}
