package com.dpm.sixpack.presentation.routes.onboarding.routes.finish

import android.R.attr.onClick
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.routes.onboarding.component.OnboardingNextButton
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.OnboardingUiState
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.finish.RecommendedGoalUiState
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.goal.GoalType
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.goal.getIconForGoalType
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun OnboardingFinishScreen(
    uiState: State<OnboardingUiState>,
    onSelectRecommendedGoal: (Int) -> Unit,
    onClickFinishButton: () -> Unit,
    onClickBackButton: () -> Unit,
    modifier: Modifier = Modifier,
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
                    enabled = uiState.value.isFinishNextEnabled,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            FinishScreenHeader()

            RecommendedGoalList(
                recommendedGoals = uiState.value.recommendedGoals,
                selectedGoalType = uiState.value.selectedGoal ?: GoalType.MARATHON,
                onSelectRecommendedGoal = onSelectRecommendedGoal,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun FinishScreenHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ill_goal_character),
            contentDescription = null,
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.onboarding_finish_title),
            style = SixpackTheme.typography.h2Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )
    }
}

@Composable
fun RecommendedGoalList(
    recommendedGoals: List<RecommendedGoalUiState>,
    selectedGoalType: GoalType,
    onSelectRecommendedGoal: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        itemsIndexed(recommendedGoals) { index, goal ->
            RecommendedGoalCard(
                imgRes = getIconForGoalType(selectedGoalType, goal),
                recommendedGoalUiState = goal,
                onSelectRecommendedGoal = { onSelectRecommendedGoal(index) }
            )
        }
    }
}

@Composable
private fun RecommendedGoalCard(
    @DrawableRes imgRes: Int,
    recommendedGoalUiState: RecommendedGoalUiState,
    onSelectRecommendedGoal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (recommendedGoalUiState.isSelected) {
        SixpackTheme.colors.blue600
    } else {
        SixpackTheme.colors.gray100
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = { onSelectRecommendedGoal() })
            .border(width = 1.dp, color = borderColor, shape = SixpackTheme.shapes.round16)
            .padding(20.dp)
    ) {
        Row {
            Image(
                imageVector = ImageVector.vectorResource(id = imgRes),
                contentDescription = null,
            )

            Spacer(Modifier.weight(1f))

            if (recommendedGoalUiState.isRecommended) {
                RecommendButton()
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = recommendedGoalUiState.title,
            style = SixpackTheme.typography.t1Bold,
            color = SixpackTheme.colors.gray900
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = recommendedGoalUiState.subTitle,
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray600
        )

        Spacer(modifier = Modifier.height(16.dp))

        GoalInfoRow(
            roundCount = recommendedGoalUiState.goalTarget.formattedRoundCount,
            duration = recommendedGoalUiState.goalTarget.formattedDuration,
            pace = recommendedGoalUiState.goalTarget.formattedPace,
            modifier = Modifier
        )
    }
}


@Composable
private fun RecommendButton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(color = SixpackTheme.colors.blue600, shape = SixpackTheme.shapes.round20)
            .padding(horizontal = 10.dp,vertical=4.dp),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = stringResource(R.string.onboarding_finish_recommend_button),
            style = SixpackTheme.typography.c1Medium,
            color =  SixpackTheme.colors.gray0
        )
    }
}

@Composable
private fun GoalInfoRow(
    roundCount: String,
    duration: String,
    pace: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        GoalInfo(
            goal = roundCount,
            description = stringResource(R.string.onboarding_finish_goal_round_count)
        )

        GoalInfoDivider()

        GoalInfo(
            goal = duration,
            description = stringResource(R.string.onboarding_finish_goal_duration)
        )

        GoalInfoDivider()

        GoalInfo(
            goal = pace,
            description = stringResource(R.string.onboarding_finish_goal_pace)
        )
    }
}

@Composable
private fun GoalInfo(
    goal: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = goal,
            style = SixpackTheme.typography.t2Bold,
            color = SixpackTheme.colors.gray900
        )

        Text(
            text = description,
            style = SixpackTheme.typography.c1Regular,
            color = SixpackTheme.colors.gray600
        )
    }
}

@Composable
fun GoalInfoDivider(modifier: Modifier = Modifier) {
    Spacer(
        modifier
            .padding(horizontal = 12.dp)
            .width(1.dp)
            .background(color = SixpackTheme.colors.gray100)
    )
}

@Preview
@Composable
private fun OnboardingFinishScreenPreview() {
    SixpackTheme {
        Surface(color = Color.White) {
            OnboardingFinishScreen(
                uiState = remember { mutableStateOf(OnboardingUiState(selectedGoal = GoalType.MARATHON)) },
                onSelectRecommendedGoal = {},
                onClickFinishButton = {},
                onClickBackButton = { },
            )
        }
    }

}
