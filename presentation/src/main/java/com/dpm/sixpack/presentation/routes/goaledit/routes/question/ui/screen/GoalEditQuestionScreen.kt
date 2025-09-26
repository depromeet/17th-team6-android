package com.dpm.sixpack.presentation.routes.goaledit.routes.question.ui.screen

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.goal.DoRunGoalCardList
import com.dpm.sixpack.presentation.common.components.goal.model.type.GoalType
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.goaledit.routes.question.contract.GoalEditQuestionScreenState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun GoalEditQuestionScreen(
    state: GoalEditQuestionScreenState,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit = {},
    onClickGoalType: (GoalType) -> Unit = {},
    onClickNext: () -> Unit = {},
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier =
            modifier
                .fillMaxSize(),
        containerColor = SixpackTheme.colors.gray0,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.home_goal_edit_title),
                        style = SixpackTheme.typography.t2Bold,
                        color = SixpackTheme.colors.gray900,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_left),
                            contentDescription = "뒤로가기", // TODO: 접근성 resource 추가
                            tint = SixpackTheme.colors.gray800,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults
                        .centerAlignedTopAppBarColors()
                        .copy(containerColor = SixpackTheme.colors.gray0),
            )
        },
        bottomBar = {
            DoRunDefaultButton(
                text = stringResource(R.string.common_next),
                onClick = onClickNext,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 24.dp)
                        .padding(horizontal = 16.dp),
                enabled = state.enableNextButton,
            )
        },
    ) { paddingValues ->

        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .scrollable(
                        state = scrollState,
                        orientation = Orientation.Vertical,
                    ),
        ) {
            Text(
                text = stringResource(R.string.home_goal_edit_question_title),
                style = SixpackTheme.typography.h2Bold,
                color = SixpackTheme.colors.gray900,
            )

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.home_goal_edit_question_description),
                style = SixpackTheme.typography.b1Regular,
                color = SixpackTheme.colors.gray600,
            )

            DoRunGoalCardList(
                modifier = Modifier.padding(top = 32.dp),
                onSelectGoal = onClickGoalType,
                selectedGoal = state.selectedGoalType,
            )
        }
    }
}

@Preview
@Composable
private fun GoalEditQuestionScreenPreview() {
    DoRunPreviewWrapper {
        GoalEditQuestionScreen(
            state = GoalEditQuestionScreenState(GoalType.MARATHON),
        )
    }
}
