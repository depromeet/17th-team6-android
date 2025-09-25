package com.dpm.sixpack.presentation.routes.onboarding.routes.finish

import androidx.compose.runtime.Composable
import com.dpm.sixpack.presentation.routes.onboarding.OnboardingViewModel
import com.dpm.sixpack.presentation.routes.onboarding.contract.OnboardingSideEffect
import com.dpm.sixpack.presentation.routes.onboarding.contract.OnboardingUiIntent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun OnboardingFinishRoute(
    viewModel: OnboardingViewModel,
    navigateToHome: () -> Unit,
    navigateToBack: () -> Unit,
) {
    val uiState = viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is OnboardingSideEffect.NavigateToHome -> navigateToHome()
            is OnboardingSideEffect.NavigateToBack -> navigateToBack()
            else -> {}
        }
    }

    OnboardingFinishScreen(
        uiState = uiState,
        onSelectRecommendedGoal = { index ->
            viewModel.onIntent(OnboardingUiIntent.SelectRecommendedGoal(index))
        },
        onClickFinishButton = {
            viewModel.onIntent(OnboardingUiIntent.CompleteOnboarding)
        },
        onClickBackButton = {
            viewModel.onIntent(OnboardingUiIntent.ClickBackButton)
        },
    )
}
