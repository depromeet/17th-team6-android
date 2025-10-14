package com.dpm.sixpack.presentation.routes.deprecated.routes.question.contract

import com.dpm.sixpack.presentation.common.base.SideEffect
import com.dpm.sixpack.presentation.common.components.deprecated.goal.model.type.GoalType

sealed interface GoalEditQuestionSideEffect : SideEffect {
    data object NavigateToBack : GoalEditQuestionSideEffect

    data class NavigateToGoalEditResult(
        val goalType: GoalType,
    ) : GoalEditQuestionSideEffect
}
