package com.dpm.sixpack.presentation.routes.onboarding.contract

import com.dpm.sixpack.presentation.common.base.UiIntent
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.goal.GoalType
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.level.LevelType
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.permission.TermType

sealed interface OnboardingUiIntent : UiIntent{
    // Permission
    data class ToggleAllTerms(
        val isChecked: Boolean,
    ) : OnboardingUiIntent

    data class ToggleTerm(
        val type: TermType,
        val isChecked: Boolean,
    ) : OnboardingUiIntent

    data class ShowTermDetails(
        val type: TermType,
    ) : OnboardingUiIntent

    data object ClickPermissionNextButton : OnboardingUiIntent

    // Level
    data class SelectLevel(
        val level: LevelType,
    ) : OnboardingUiIntent
    data object ClickLevelNextButton : OnboardingUiIntent

    // Goal
    data class SelectGoal(
        val goal: GoalType,
    ) : OnboardingUiIntent
    data object ClickGoalNextButton : OnboardingUiIntent

    // Finish
    data object CompleteOnboarding : OnboardingUiIntent

    data class SelectRecommendedGoal(
        val id: Int,
    ) : OnboardingUiIntent

    // Common
    data object ClickBackButton : OnboardingUiIntent
}
