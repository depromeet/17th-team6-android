package com.dpm.sixpack.presentation.routes.onboarding.routes.goal

import androidx.compose.runtime.Composable
import com.dpm.sixpack.presentation.routes.onboarding.OnboardingViewModel
import com.dpm.sixpack.presentation.routes.onboarding.contract.OnboardingSideEffect
import com.dpm.sixpack.presentation.routes.onboarding.contract.OnboardingUiIntent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun OnboardingGoalRoute(
    viewModel: OnboardingViewModel,
    navigateToFinish: () -> Unit,
    navigateToBack: () -> Unit,
) {
    val uiState = viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is OnboardingSideEffect.NavigateToFinish -> navigateToFinish()
            is OnboardingSideEffect.NavigateToBack -> navigateToBack()
            else -> {}
        }
    }

    OnboardingGoalScreen(
        uiState = uiState,
        onSelectGoal = { goal ->
            viewModel.onIntent(OnboardingUiIntent.SelectGoal(goal))
        },
        onClickNextButton = {
            viewModel.onIntent(OnboardingUiIntent.ClickGoalNextButton)
        },
        onClickBackButton = {
            viewModel.onIntent(OnboardingUiIntent.ClickBackButton)
        },
    )
}
