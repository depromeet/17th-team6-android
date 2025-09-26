package com.dpm.sixpack.presentation.routes.goaledit.routes.question.contract

import com.dpm.sixpack.presentation.common.base.SideEffect
import com.dpm.sixpack.presentation.routes.goaledit.common.model.GoalEditGoalType

sealed interface GoalEditQuestionSideEffect : SideEffect {
    data object NavigateToBack : GoalEditQuestionSideEffect

    data class NavigateToGoalEditResult(
        val goalType: GoalEditGoalType,
    ) : GoalEditQuestionSideEffect
}
