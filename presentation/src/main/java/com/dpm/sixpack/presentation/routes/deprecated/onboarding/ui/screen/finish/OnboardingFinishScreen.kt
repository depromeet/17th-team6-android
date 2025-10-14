package com.dpm.sixpack.presentation.routes.deprecated.onboarding.ui.screen.finish

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.deprecated.goal.DoRunRecommendedGoalList
import com.dpm.sixpack.presentation.common.components.deprecated.goal.model.state.GoalUiState
import com.dpm.sixpack.presentation.common.components.deprecated.goal.model.state.RecommendedGoalUiState
import com.dpm.sixpack.presentation.common.components.deprecated.goal.model.type.GoalType
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.contract.uistate.OnboardingUiState
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.ui.component.common.OnboardingNextButton
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.ui.component.finish.FinishScreenHeader
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

            DoRunRecommendedGoalList(
                recommendedGoals = uiState.recommendedGoals,
                selectedGoalType = uiState.selectedGoal ?: GoalType.getDefault(),
                onSelectRecommendedGoal = onSelectRecommendedGoal,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
    }
}

@Preview
@Composable
private fun OnboardingFinishScreenPreview() {
    DoRunPreviewWrapper {
        OnboardingFinishScreen(
            uiState =
                OnboardingUiState(
                    recommendedGoals =
                        listOf(
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
                                        roundCount = 50,
                                    ),
                            ),
                        ),
                ),
        )
    }
}
