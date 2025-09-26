package com.dpm.sixpack.presentation.common.components.goal.model.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecommendedGoalUiState(
    val title: String,
    val subTitle: String,
    val goalTarget: GoalUiState,
    val isRecommended: Boolean,
    val isSelected: Boolean,
) : Parcelable
