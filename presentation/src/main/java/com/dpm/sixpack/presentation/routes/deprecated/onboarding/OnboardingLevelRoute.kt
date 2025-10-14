package com.dpm.sixpack.presentation.routes.deprecated.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.contract.OnboardingSideEffect
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.contract.OnboardingUiIntent
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.ui.screen.level.OnboardingLevelScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun OnboardingLevelRoute(
    viewModel: OnboardingViewModel,
    navigateToGoal: () -> Unit,
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is OnboardingSideEffect.NavigateToGoal -> navigateToGoal()
            is OnboardingSideEffect.NavigateToBack -> navigateToBack()
            else -> {}
        }
    }

    OnboardingLevelScreen(
        uiState = uiState,
        onSelectLevel = { level ->
            viewModel.onIntent(OnboardingUiIntent.SelectLevel(level))
        },
        onClickNextButton = {
            viewModel.onIntent(OnboardingUiIntent.ClickLevelNextButton)
        },
        onClickBackButton = {
            viewModel.onIntent(OnboardingUiIntent.ClickBackButton)
        },
    )
}
