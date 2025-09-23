package com.dpm.sixpack.presentation.routes.home.contract

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeScreenState(
    val totalGoalComponentState: HomeTotalGoalComponentState = HomeTotalGoalComponentState(),
    val sessionComponentState: HomeSessionComponentState = HomeSessionComponentState(),
) : UiState, Parcelable

@Parcelize
data class HomeTotalGoalComponentState(
    val loading: Boolean = false,
    @DrawableRes val imageRes: Int? = null, // TODO enum 으로 변경?
    val title: String = "",
    val distance: String = "",
    val duration: String = "",
    val pace: String = ""
) : Parcelable

@Parcelize
data class HomeSessionComponentState(
    val loading: Boolean = false,
    val sessionCount: Int? = null,
    @StringRes val cheerUpStringRes: Int? = null, // TODO enum 으로 변경?
    val distance: String = "",
    val duration: String = "",
    val pace: String = ""
) : Parcelable {
    val showPreviousSession: Boolean
        get() = (sessionCount ?: 0) > 1
}

