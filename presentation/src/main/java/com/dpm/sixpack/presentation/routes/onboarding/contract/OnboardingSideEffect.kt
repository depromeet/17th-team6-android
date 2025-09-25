package com.dpm.sixpack.presentation.routes.onboarding.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface OnboardingSideEffect : SideEffect {
    data object NavigateToLevel : OnboardingSideEffect

    data object NavigateToGoal : OnboardingSideEffect

    data object NavigateToFinish : OnboardingSideEffect

    data object NavigateToBack : OnboardingSideEffect

    data object NavigateToHome : OnboardingSideEffect
}
