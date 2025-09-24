package com.dpm.sixpack.presentation.routes.onboarding.level.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface OnboardingSideEffect : SideEffect {
    data object NavigateToNext : OnboardingSideEffect
}
