package com.dpm.sixpack.presentation.routes.goaledit.routes.result.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.common.components.goal.model.state.RecommendedGoalUiState
import com.dpm.sixpack.presentation.common.components.goal.model.type.GoalType
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class GoalEditResultScreenState(
    val loading: Boolean = true,
    val selectedGoal: GoalType? = null,
    val recommendedGoals: List<RecommendedGoalUiState> = emptyList(),
) : UiState,
    Parcelable {
    @IgnoredOnParcel
    val enableNextButton: Boolean = recommendedGoals.any { it.isSelected }
}
