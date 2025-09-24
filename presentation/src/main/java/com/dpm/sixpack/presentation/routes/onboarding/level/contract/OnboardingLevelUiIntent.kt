package com.dpm.sixpack.presentation.routes.onboarding.level.contract

import com.dpm.sixpack.presentation.common.base.UiIntent
import com.dpm.sixpack.presentation.routes.onboarding.level.contract.uistate.RunningLevel

sealed interface OnboardingLevelUiIntent : UiIntent{
    data class SelectLevel(
        val level: RunningLevel,
    ) : OnboardingLevelUiIntent

    data object ClickNextButton : OnboardingLevelUiIntent

    data object ClickBackButton : OnboardingLevelUiIntent
}
