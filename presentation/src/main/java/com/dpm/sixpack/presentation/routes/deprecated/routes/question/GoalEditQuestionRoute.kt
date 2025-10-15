package com.dpm.sixpack.presentation.routes.deprecated.routes.question

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.deprecated.routes.question.contract.GoalEditQuestionIntent
import com.dpm.sixpack.presentation.routes.deprecated.routes.question.contract.GoalEditQuestionSideEffect
import com.dpm.sixpack.presentation.routes.deprecated.routes.question.ui.screen.GoalEditQuestionScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun GoalEditQuestionRoute(
    modifier: Modifier = Modifier,
    viewModel: GoalEditQuestionViewModel = hiltViewModel(),
    onNavigateToBack: () -> Unit = {},
    onNavigateToGoalEditResult: () -> Unit = {},
) {
    val screenState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is GoalEditQuestionSideEffect.NavigateToBack -> {
                onNavigateToBack()
            }

            is GoalEditQuestionSideEffect.NavigateToGoalEditResult -> {
                onNavigateToGoalEditResult()
            }
        }
    }

    GoalEditQuestionScreen(
        modifier = modifier,
        state = screenState,
        onClickGoalType = {
            viewModel.onIntent(GoalEditQuestionIntent.GoalTypeClick(it))
        },
        onClickBack = {
            viewModel.onIntent(GoalEditQuestionIntent.BackClick)
        },
        onClickNext = {
            viewModel.onIntent(GoalEditQuestionIntent.NextClick)
        },
    )
}
