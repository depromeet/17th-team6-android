package com.dpm.sixpack.presentation.routes.goaledit.routes.question.contract

import com.dpm.sixpack.presentation.common.base.UiIntent
import com.dpm.sixpack.presentation.common.components.goal.model.type.GoalType

sealed interface GoalEditQuestionIntent : UiIntent {
    data object BackClick : GoalEditQuestionIntent

    data class GoalTypeClick(
        val goalType: GoalType,
    ) : GoalEditQuestionIntent

    data object NextClick : GoalEditQuestionIntent
}
