package com.dpm.sixpack.presentation.routes.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.home.contract.HomeIntent
import com.dpm.sixpack.presentation.routes.home.contract.HomeSideEffect
import com.dpm.sixpack.presentation.routes.home.screen.HomeScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSession: (sessionId: Long) -> Unit,
    onNavigateToGoalList: (goalId: Long) -> Unit,
    onNavigateToGoalEdit: () -> Unit,
) {
    val screenState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is HomeSideEffect.NavigateToSession -> {
                onNavigateToSession(sideEffect.sessionId)
            }

            is HomeSideEffect.NavigateToGoalList -> {
                onNavigateToGoalList(sideEffect.goalId)
            }

            is HomeSideEffect.NavigateToGoalEdit -> {
                onNavigateToGoalEdit()
            }
        }
    }

    HomeScreen(
        modifier = modifier,
        uiState = screenState,
        onClickPreviousSession = {
            viewModel.onIntent(HomeIntent.PreviousSession)
        },
        onClickNextSession = {
            viewModel.onIntent(HomeIntent.NextSession)
        },
        onNavigateToGoalList = {
            viewModel.onIntent(HomeIntent.GoalList)
        },
        onNavigateToGoalEdit = {
            viewModel.onIntent(HomeIntent.GoalEdit)
        },
    )
}
