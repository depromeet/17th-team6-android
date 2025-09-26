package com.dpm.sixpack.presentation.routes.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.dpm.sixpack.presentation.routes.onboarding.contract.OnboardingSideEffect
import com.dpm.sixpack.presentation.routes.onboarding.contract.OnboardingUiIntent
import com.dpm.sixpack.presentation.routes.onboarding.ui.screen.permission.OnboardingPermissionScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun OnboardingPermissionRoute(
    viewModel: OnboardingViewModel,
    navigateToLevel: () -> Unit,
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is OnboardingSideEffect.NavigateToLevel -> navigateToLevel()
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
