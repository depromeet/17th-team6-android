package com.dpm.sixpack.presentation.routes.goaledit.routes.question.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.routes.goaledit.common.model.GoalEditGoalType
import kotlinx.parcelize.Parcelize

@Parcelize
data class GoalEditQuestionScreenState(
    val goalTypes: List<GoalEditGoalTypeComponentState> =
        GoalEditGoalType.entries.map {
            GoalEditGoalTypeComponentState(goalType = it)
        },
) : UiState,
    Parcelable {
    val enableNextButton: Boolean = goalTypes.any { it.isSelected }
}

@Parcelize
data class GoalEditGoalTypeComponentState(
    val isSelected: Boolean = false,
    val goalType: GoalEditGoalType,
) : Parcelable
