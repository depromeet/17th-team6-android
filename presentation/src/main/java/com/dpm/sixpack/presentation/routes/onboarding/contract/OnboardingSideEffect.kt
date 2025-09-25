package com.dpm.sixpack.presentation.routes.onboarding.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface OnboardingSideEffect : SideEffect {
    data object NavigateToLevelScreen : OnboardingSideEffect
    data object NavigateToGoalScreen : OnboardingSideEffect
    data object NavigateToFinishScreen : OnboardingSideEffect
    data object NavigateToBack : OnboardingSideEffect

    data object CompleteOnboarding : OnboardingSideEffect
}
