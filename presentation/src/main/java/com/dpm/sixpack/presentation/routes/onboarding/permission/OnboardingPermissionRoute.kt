package com.dpm.sixpack.presentation.routes.onboarding.permission

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.onboarding.permission.contract.OnboardingPermissionIntent
import com.dpm.sixpack.presentation.routes.onboarding.permission.contract.OnboardingPermissionSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun OnboardingPermissionRoute(
    navigateToNext: () -> Unit,
    navigateToBack: () -> Unit,
    viewModel: OnboardingPermissionViewModel = hiltViewModel(),
) {
    val uiState = viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is OnboardingPermissionSideEffect.NavigateToNext -> navigateToNext()
            is OnboardingPermissionSideEffect.NavigateToBack -> navigateToBack()
        }
    }

    OnboardingPermissionScreen(
        uiState = uiState,
        onToggleAllTerms = { isChecked ->
            viewModel.onIntent(OnboardingPermissionIntent.ToggleAllTerms(isChecked))
        },
        onToggleTerm = { type, isChecked ->
            viewModel.onIntent(OnboardingPermissionIntent.ToggleTerm(type, isChecked))
        },
        onClickNextButton = {
            viewModel.onIntent(OnboardingPermissionIntent.ClickNextButton)
        },
        onClickBackButton = {
            viewModel.onIntent(OnboardingPermissionIntent.ClickBackButton)
        },
    )
}
