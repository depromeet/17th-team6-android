package com.dpm.sixpack.presentation.routes.onboarding

import androidx.compose.runtime.Composable
import com.dpm.sixpack.presentation.routes.onboarding.ui.screen.OnboardingScreen

@Composable
fun OnboardingRoute(
    onNavigateToSignUp: () -> Unit,
    onNavigateToSignIn: () -> Unit,
) {
    OnboardingScreen(
        onClickSignUp = onNavigateToSignUp,
        onClickSignIn = onNavigateToSignIn,
    )
}
