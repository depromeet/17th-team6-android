package com.dpm.sixpack.presentation.routes.onboarding.permission.contract

import com.dpm.sixpack.presentation.common.base.UiIntent
import com.dpm.sixpack.presentation.routes.onboarding.permission.contract.uistate.TermType

sealed interface OnboardingPermissionIntent : UiIntent {
    data class ToggleAllTerms(val isChecked: Boolean) : OnboardingPermissionIntent
    data class ToggleTerm(val type: TermType, val isChecked: Boolean) : OnboardingPermissionIntent
    data class ShowTermDetails(val type: TermType) : OnboardingPermissionIntent
    data object ClickNextButton : OnboardingPermissionIntent
    data object ClickBackButton : OnboardingPermissionIntent
}
