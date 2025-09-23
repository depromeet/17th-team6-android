package com.dpm.sixpack.presentation.routes.home.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeScreenState(
    val totalGoalComponentState: HomeTotalGoalComponentState = HomeTotalGoalComponentState(),
    val nextSessionComponentState: HomeNextSessionComponentState = HomeNextSessionComponentState(),
) : UiState, Parcelable

@Parcelize
data class HomeTotalGoalComponentState(
    val loading: Boolean = false,
    val title: String = "",
    val distance: String = "",
    val duration: String = "",
    val pace: String = ""
) : Parcelable

@Parcelize
data class HomeNextSessionComponentState(
    val loading: Boolean = false,
    val sessionCount: Int? = null,
    val date: String = "",
    val distance: String = "",
    val duration: String = "",
    val pace: String = ""
) : Parcelable {
    val hasPreviousSession: Boolean
        get() = (sessionCount ?: 0) > 1
}

