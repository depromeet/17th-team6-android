package com.dpm.sixpack.presentation.routes.onboarding.contract

import com.dpm.sixpack.presentation.common.base.UiIntent
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.level.RunningLevel
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
        val level: RunningLevel,
    ) : OnboardingUiIntent

    data object ClickLevelNextButton : OnboardingUiIntent


    data object ClickBackButton : OnboardingUiIntent
}
