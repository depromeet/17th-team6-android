package com.dpm.sixpack.presentation.routes.goaledit.routes.question.contract

import com.dpm.sixpack.presentation.common.base.UiIntent
import com.dpm.sixpack.presentation.routes.goaledit.common.model.GoalEditGoalType

sealed interface GoalEditQuestionIntent : UiIntent {
    data object BackClick : GoalEditQuestionIntent

    data class GoalTypeClick(
        val goalType: GoalEditGoalType,
    ) : GoalEditQuestionIntent

    data object NextClick : GoalEditQuestionIntent
}
