package com.dpm.sixpack.presentation.destinations

import kotlinx.serialization.Serializable

@Deprecated("온보딩 변경")
@Serializable
sealed interface OnboardingRoute : Route {
    @Serializable
    data object Onboarding : OnboardingRoute

    @Serializable
    data object Permission : OnboardingRoute

    @Serializable
    data object LevelSelection : OnboardingRoute

    @Serializable
    data object GoalSelection : OnboardingRoute

    @Serializable
    data object Finish : OnboardingRoute
}
