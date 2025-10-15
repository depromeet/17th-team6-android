package com.dpm.sixpack.presentation.routes.deprecated.routes.result.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.deprecated.goal.DoRunRecommendedGoalList
import com.dpm.sixpack.presentation.common.components.deprecated.goal.model.state.GoalUiState
import com.dpm.sixpack.presentation.common.components.deprecated.goal.model.state.RecommendedGoalUiState
import com.dpm.sixpack.presentation.common.components.deprecated.goal.model.type.GoalType
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.deprecated.routes.result.contract.GoalEditResultScreenState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun GoalEditResultScreen(
    state: GoalEditResultScreenState,
    modifier: Modifier = Modifier,
    onClickRecommendedGoal: (index: Int) -> Unit = {},
    onClickComplete: () -> Unit = {},
    onClickBack: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
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
                onClick = onClickComplete,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 24.dp)
                        .padding(horizontal = 16.dp),
                enabled = state.enableNextButton,
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier =
                    modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth(),
                horizontalAlignment = Alignment.Companion.CenterHorizontally,
            ) {
                Image(
                    imageVector = ImageVector.Companion.vectorResource(id = R.drawable.ill_goal_character),
                    contentDescription = null,
                )

                Text(
                    text = stringResource(R.string.home_goal_edit_result_title),
                    style = SixpackTheme.typography.h2Bold,
                    modifier =
                        Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                    textAlign = TextAlign.Center,
                )
            }

            DoRunRecommendedGoalList(
                recommendedGoals = state.recommendedGoals,
                selectedGoalType = state.selectedGoal ?: GoalType.getDefault(),
                onSelectRecommendedGoal = onClickRecommendedGoal,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
    }
}

@Preview
@Composable
private fun GoalEditResultScreenPreview() {
    DoRunPreviewWrapper {
        GoalEditResultScreen(
            state =
                GoalEditResultScreenState(
                    recommendedGoals =
                        listOf(
                            RecommendedGoalUiState(
                                title = "마라톤 완주",
                                subTitle = "42.195km를 완주할 수 있어요",
                                isRecommended = true,
                                isSelected = false,
                                goalTarget =
                                    GoalUiState(
                                        distance = 500,
                                        duration = 300,
                                        pace = 3000,
                                        roundCount = 50,
                                    ),
                            ),
                        ),
                ),
        )
    }
}
