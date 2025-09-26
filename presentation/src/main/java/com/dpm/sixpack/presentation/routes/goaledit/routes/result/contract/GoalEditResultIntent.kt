package com.dpm.sixpack.presentation.routes.goaledit.routes.result.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface GoalEditResultIntent : UiIntent {
    data object BackClick : GoalEditResultIntent

    data class RecommendedGoalClick(
        val index: Int,
    ) : GoalEditResultIntent

    data object CompleteClick : GoalEditResultIntent
}
