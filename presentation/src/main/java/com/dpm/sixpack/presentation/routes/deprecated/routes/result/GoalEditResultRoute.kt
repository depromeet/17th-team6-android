package com.dpm.sixpack.presentation.routes.deprecated.routes.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.deprecated.routes.result.contract.GoalEditResultIntent
import com.dpm.sixpack.presentation.routes.deprecated.routes.result.contract.GoalEditResultSideEffect
import com.dpm.sixpack.presentation.routes.deprecated.routes.result.ui.GoalEditResultScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun GoalEditResultRoute(
    modifier: Modifier = Modifier,
    viewModel: GoalEditResultViewModel = hiltViewModel<GoalEditResultViewModel>(),
    onNavigateToBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
) {
    val screenState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is GoalEditResultSideEffect.NavigateToBack -> onNavigateToBack()
            is GoalEditResultSideEffect.NavigateToHome -> onNavigateToHome()
        }
    }

    GoalEditResultScreen(
        state = screenState,
        modifier = modifier,
        onClickRecommendedGoal = { index ->
            viewModel.onIntent(GoalEditResultIntent.RecommendedGoalClick(index))
        },
        onClickComplete = {
            viewModel.onIntent(GoalEditResultIntent.CompleteClick)
        },
        onClickBack = {
            viewModel.onIntent(GoalEditResultIntent.BackClick)
        },
    )
}
