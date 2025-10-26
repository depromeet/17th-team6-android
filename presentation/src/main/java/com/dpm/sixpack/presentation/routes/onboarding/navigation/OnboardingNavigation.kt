package com.dpm.sixpack.presentation.routes.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.OnboardingRoute
import com.dpm.sixpack.presentation.routes.onboarding.OnboardingRoute

fun NavGraphBuilder.addOnboardingNavGraph(
    onNavigateToSignUp: () -> Unit,
    onNavigateToSignIn: () -> Unit,
) {
    composable<OnboardingRoute> {
        OnboardingRoute(
            onNavigateToSignUp = onNavigateToSignUp,
            onNavigateToSignIn = onNavigateToSignIn,
        )
    }
}
