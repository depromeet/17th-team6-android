package com.dpm.sixpack.presentation.routes.deprecated.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.deprecated.home.contract.HomeIntent
import com.dpm.sixpack.presentation.routes.deprecated.home.contract.HomeSideEffect
import com.dpm.sixpack.presentation.routes.deprecated.home.ui.screen.HomeScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSessionList: (goalId: Long) -> Unit,
    onNavigateToSession: (sessionId: Long) -> Unit,
    onNavigateToGoalEdit: () -> Unit,
) {
    val screenState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is HomeSideEffect.NavigateToSession -> {
                onNavigateToSession(sideEffect.sessionId)
            }

            is HomeSideEffect.NavigateToSessionList -> {
                onNavigateToSessionList(sideEffect.goalId)
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
