package com.dpm.sixpack.presentation.routes.onboarding.permission.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface OnboardingPermissionSideEffect : SideEffect {
    data object NavigateToNext : OnboardingPermissionSideEffect

    data object NavigateToBack : OnboardingPermissionSideEffect
}
