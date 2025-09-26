package com.dpm.sixpack.presentation.routes.goaledit.routes.question

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.routes.goaledit.routes.question.contract.GoalEditQuestionIntent
import com.dpm.sixpack.presentation.routes.goaledit.routes.question.contract.GoalEditQuestionSideEffect
import com.dpm.sixpack.presentation.routes.goaledit.routes.question.ui.screen.GoalEditQuestionScreen
import com.dpm.sixpack.presentation.theme.SixpackTheme
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
