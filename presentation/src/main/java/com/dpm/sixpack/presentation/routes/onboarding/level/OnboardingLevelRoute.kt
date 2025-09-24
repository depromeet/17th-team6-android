package com.dpm.sixpack.presentation.routes.onboarding.level

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dpm.sixpack.presentation.routes.onboarding.OnboardingViewModel
import com.dpm.sixpack.presentation.routes.onboarding.contract.OnboardingSideEffect
import com.dpm.sixpack.presentation.routes.onboarding.contract.OnboardingUiIntent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun OnboardingLevelRoute(
    viewModel: OnboardingViewModel,
    navigateToGoal: () -> Unit,
    navigateToBack: () -> Unit,
) {
    val uiState = viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is OnboardingSideEffect.NavigateToGoalScreen -> navigateToGoal()
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
            viewModel.onIntent(OnboardingUiIntent.ClickPermissionNextButton)
        },
        onClickBackButton = {
            viewModel.onIntent(OnboardingUiIntent.ClickBackButton)
        },
    )
}
