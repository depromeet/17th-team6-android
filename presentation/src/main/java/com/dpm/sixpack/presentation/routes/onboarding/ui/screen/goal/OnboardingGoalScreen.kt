package com.dpm.sixpack.presentation.routes.onboarding.ui.screen.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.goal.DoRunGoalCardList
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.routes.onboarding.ui.component.common.OnboardingNextButton
import com.dpm.sixpack.presentation.routes.onboarding.ui.component.common.OnboardingPage
import com.dpm.sixpack.presentation.routes.onboarding.ui.component.common.OnboardingPageIndicator
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.OnboardingUiState
import com.dpm.sixpack.presentation.common.components.goal.model.type.GoalType
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.level.LevelType
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun OnboardingGoalScreen(
    uiState: OnboardingUiState,
    modifier: Modifier = Modifier,
    onSelectGoal: (GoalType) -> Unit = {},
    onClickNextButton: () -> Unit = {},
    onClickBackButton: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = SixpackTheme.colors.gray0),
    ) {
        DoRunNavigationTopBar(
            navigateToBack = onClickBackButton,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            OnboardingPageIndicator(page = OnboardingPage.GOAL)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.onboarding_goal_title),
                style = SixpackTheme.typography.h2Bold,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(32.dp))

            DoRunGoalCardList(
                selectedGoal = uiState.selectedGoal,
                onSelectGoal = onSelectGoal,
                modifier =
                    Modifier
                        .fillMaxWidth(),
            )

            Spacer(Modifier.weight(1f))

            OnboardingNextButton(
                onClick = onClickNextButton,
                enabled = uiState.isGoalNextEnabled,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview
@Composable
private fun OnboardingGoalScreenPreview() {
    DoRunPreviewWrapper {
        OnboardingGoalScreen(
            uiState = OnboardingUiState(
                selectedLevel = LevelType.CONSISTENT,
                selectedGoal = GoalType.MARATHON,
                recommendedGoals = listOf()
            ),
        )
    }
}
