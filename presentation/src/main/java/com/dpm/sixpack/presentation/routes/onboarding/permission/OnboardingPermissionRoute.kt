package com.dpm.sixpack.presentation.routes.onboarding.permission

import androidx.compose.runtime.Composable
import com.dpm.sixpack.presentation.routes.onboarding.OnboardingViewModel
import com.dpm.sixpack.presentation.routes.onboarding.contract.OnboardingSideEffect
import com.dpm.sixpack.presentation.routes.onboarding.contract.OnboardingUiIntent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun OnboardingPermissionRoute(
    viewModel: OnboardingViewModel,
    navigateToLevel: () -> Unit,
    navigateToBack: () -> Unit,
) {
    val uiState = viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is OnboardingSideEffect.NavigateToLevelScreen -> navigateToLevel()
            is OnboardingSideEffect.NavigateToBack -> {}
            else -> {}
        }
    }

    OnboardingPermissionScreen(
        uiState = uiState,
        onToggleAllTerms = { isChecked ->
            viewModel.onIntent(OnboardingUiIntent.ToggleAllTerms(isChecked))
        },
        onToggleTerm = { type, isChecked ->
            viewModel.onIntent(OnboardingUiIntent.ToggleTerm(type, isChecked))
        },
        onClickNextButton = {
            viewModel.onIntent(OnboardingUiIntent.ClickPermissionNextButton)
        },
        onClickBackButton = {
            viewModel.onIntent(OnboardingUiIntent.ClickBackButton)
        },
    )
}
