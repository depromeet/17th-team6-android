package com.dpm.sixpack.presentation.routes.onboarding.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.onboarding.ui.component.navigation.OnboardingNavigationComponent
import com.dpm.sixpack.presentation.routes.onboarding.ui.component.pager.OnboardingPagerComponent
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onClickSignUp: () -> Unit = {},
    onClickSignIn: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            OnboardingNavigationComponent(
                onClickSignUp = onClickSignUp,
                onClickSignIn = onClickSignIn
            )
        },
        containerColor = SixpackTheme.colors.gray0
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = BiasAlignment(horizontalBias = 0.5f, verticalBias = 0.1f)
        ) {
            OnboardingPagerComponent()
        }
    }
}

@Preview
@Composable
private fun OnboardingScreenPreview() {
    DoRunPreviewWrapper {
        OnboardingScreen()
    }
}

