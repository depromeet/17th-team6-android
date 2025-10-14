package com.dpm.sixpack.presentation.routes.deprecated.home.contract

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.common.base.UiState
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
