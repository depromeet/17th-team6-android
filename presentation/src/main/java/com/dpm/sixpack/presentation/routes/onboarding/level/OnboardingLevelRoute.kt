package com.dpm.sixpack.presentation.routes.onboarding.level

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.onboarding.level.contract.OnboardingLevelSideEffect
import com.dpm.sixpack.presentation.routes.onboarding.level.contract.OnboardingLevelUiIntent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun OnboardingLevelRoute(
    navigateToNext: () -> Unit,
    navigateToBack: () -> Unit,
    viewModel: OnboardingLevelViewModel = hiltViewModel(),
) {
    val uiState = viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is OnboardingLevelSideEffect.NavigateToNext -> navigateToNext()
            is OnboardingLevelSideEffect.NavigateToBack -> navigateToBack()
        }
    }

    OnboardingLevelScreen(
        uiState = uiState,
        onSelectLevel = { level ->
            viewModel.onIntent(OnboardingLevelUiIntent.SelectLevel(level))
        },
        onClickNextButton = {
            viewModel.onIntent(OnboardingLevelUiIntent.ClickNextButton)
        },
        onClickBackButton = {
            viewModel.onIntent(OnboardingLevelUiIntent.ClickBackButton)
        },
    )
}
