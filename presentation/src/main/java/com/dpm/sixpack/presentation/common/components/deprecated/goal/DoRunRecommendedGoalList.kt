package com.dpm.sixpack.presentation.common.components.deprecated.goal

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.deprecated.goal.model.state.RecommendedGoalUiState
import com.dpm.sixpack.presentation.common.components.deprecated.goal.model.type.GoalType
import com.dpm.sixpack.presentation.common.components.deprecated.goal.model.type.getIconForGoalType
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun DoRunRecommendedGoalList(
    recommendedGoals: List<RecommendedGoalUiState>,
    selectedGoalType: GoalType,
    onSelectRecommendedGoal: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        itemsIndexed(recommendedGoals) { index, goal ->
            RecommendedGoalCard(
                imgRes = getIconForGoalType(selectedGoalType, goal),
                recommendedGoalUiState = goal,
                onSelectRecommendedGoal = { onSelectRecommendedGoal(index) },
            )
        }
    }
}

@Composable
private fun RecommendedGoalCard(
    @DrawableRes imgRes: Int,
    recommendedGoalUiState: RecommendedGoalUiState,
    onSelectRecommendedGoal: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor =
        if (recommendedGoalUiState.isSelected) {
            SixpackTheme.colors.blue600
        } else {
            SixpackTheme.colors.gray100
        }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .noRippleClickable(onClick = { onSelectRecommendedGoal() })
                .border(width = 1.dp, color = borderColor, shape = SixpackTheme.shapes.round16)
                .padding(20.dp),
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
            color = SixpackTheme.colors.gray900,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = recommendedGoalUiState.subTitle,
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray600,
        )

        Spacer(modifier = Modifier.height(16.dp))

        GoalInfoRow(
            roundCount = recommendedGoalUiState.goalTarget.formattedRoundCount,
            duration = recommendedGoalUiState.goalTarget.formattedDuration,
            pace = recommendedGoalUiState.goalTarget.formattedPace,
            modifier = Modifier,
        )
    }
}

@Composable
private fun RecommendButton(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .background(color = SixpackTheme.colors.blue600, shape = SixpackTheme.shapes.round20)
                .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.onboarding_finish_recommend_button),
            style = SixpackTheme.typography.c1Medium,
            color = SixpackTheme.colors.gray0,
        )
    }
}

@Composable
private fun GoalInfoRow(
    roundCount: String,
    duration: String,
    pace: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        GoalInfo(
            goal = roundCount,
            description = stringResource(R.string.onboarding_finish_goal_round_count),
        )

        GoalInfoDivider()

        GoalInfo(
            goal = duration,
            description = stringResource(R.string.onboarding_finish_goal_duration),
        )

        GoalInfoDivider()

        GoalInfo(
            goal = pace,
            description = stringResource(R.string.onboarding_finish_goal_pace),
        )
    }
}

@Composable
private fun GoalInfo(
    goal: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = goal,
            style = SixpackTheme.typography.t2Bold,
            color = SixpackTheme.colors.gray900,
        )

        Text(
            text = description,
            style = SixpackTheme.typography.c1Regular,
            color = SixpackTheme.colors.gray600,
        )
    }
}

@Composable
fun GoalInfoDivider(modifier: Modifier = Modifier) {
    Spacer(
        modifier
            .padding(horizontal = 12.dp)
            .width(1.dp)
            .background(color = SixpackTheme.colors.gray100),
    )
}
