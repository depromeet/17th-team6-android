package com.dpm.sixpack.presentation.routes.onboarding.routes.finish

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.goal.RecommendedGoalList
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.routes.onboarding.component.OnboardingNextButton
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.OnboardingUiState
import com.dpm.sixpack.presentation.common.components.goal.model.state.GoalUiState
import com.dpm.sixpack.presentation.common.components.goal.model.state.RecommendedGoalUiState
import com.dpm.sixpack.presentation.common.components.goal.model.type.GoalType
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun OnboardingFinishScreen(
    uiState: OnboardingUiState,
    modifier: Modifier = Modifier,
    onSelectRecommendedGoal: (Int) -> Unit = {},
    onClickFinishButton: () -> Unit = {},
    onClickBackButton: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SixpackTheme.colors.gray0,
        topBar = {
            DoRunNavigationTopBar(
                navigateToBack = onClickBackButton,
            )
        },
        bottomBar = {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(12.dp))

                OnboardingNextButton(
                    text = R.string.onboarding_finish_button,
                    onClick = onClickFinishButton,
                    enabled = uiState.isFinishNextEnabled,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            FinishScreenHeader()

            RecommendedGoalList(
                recommendedGoals = uiState.recommendedGoals,
                selectedGoalType = uiState.selectedGoal ?: GoalType.MARATHON,
                onSelectRecommendedGoal = onSelectRecommendedGoal,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
    }
}

@Composable
private fun FinishScreenHeader(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ill_goal_character),
            contentDescription = null,
            modifier = Modifier,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.onboarding_finish_title),
            style = SixpackTheme.typography.h2Bold,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
        )
    }
}


@Preview
@Composable
private fun OnboardingFinishScreenPreview() {
    DoRunPreviewWrapper {
        OnboardingFinishScreen(
            uiState = OnboardingUiState(
                recommendedGoals = listOf(
                    RecommendedGoalUiState(
                        title = "마라톤 완주",
                        subTitle = "42.195km를 완주할 수 있어요",
                        isRecommended = true,
                        isSelected = true,
                        goalTarget =
                            GoalUiState(
                                distance = 500,
                                duration = 300,
                                pace = 3000,
                                roundCount = 50
                            ),
                    )
                )
            ),
        )
    }
}

