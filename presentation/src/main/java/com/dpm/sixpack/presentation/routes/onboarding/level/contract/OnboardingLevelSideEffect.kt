package com.dpm.sixpack.presentation.routes.onboarding.level.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface OnboardingLevelSideEffect : SideEffect {
    data object NavigateToNext : OnboardingLevelSideEffect

    data object NavigateToBack : OnboardingLevelSideEffect
}
