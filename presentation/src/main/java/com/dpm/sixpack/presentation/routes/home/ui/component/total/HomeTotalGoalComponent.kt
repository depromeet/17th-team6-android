package com.dpm.sixpack.presentation.routes.home.ui.component.total

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunTotalGoalProgress
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.home.ui.component.total.item.HomeTotalGoalInformationItem
import com.dpm.sixpack.presentation.routes.home.ui.component.total.sub.HomeTotalGoalCompletedSubComponent
import com.dpm.sixpack.presentation.routes.home.ui.component.total.sub.TextButtonWithIcon
import com.dpm.sixpack.presentation.routes.home.contract.HomeTotalGoalComponentState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun HomeTotalGoalComponent(
    modifier: Modifier = Modifier,
    totalGoalCompleted: Boolean = false,
    state: HomeTotalGoalComponentState,
    onNavigateToGoalList: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 20.dp)
                .padding(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                state.imageRes?.let {
                    Image(
                        modifier = Modifier.padding(end = 8.dp),
                        painter = painterResource(it),
                        contentDescription = "운동 목표 일러스트", // TODO: 접근성 resource 추가
                    )
                }
                Text(
                    text = state.title,
                    style = SixpackTheme.typography.t1Bold,
                    color = SixpackTheme.colors.gray900,
                )
            }

            TextButtonWithIcon(
                title = stringResource(R.string.home_goal_list),
                onClick = onNavigateToGoalList,
            )
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HomeTotalGoalInformationItem(
                modifier = Modifier,
                value = state.distance,
                title = stringResource(R.string.home_goal_distance),
            )
            VerticalDivider(
                modifier = Modifier.height(20.dp),
                color = SixpackTheme.colors.gray100,
                thickness = 1.dp,
            )
            HomeTotalGoalInformationItem(
                modifier = Modifier,
                value = state.duration,
                title = stringResource(R.string.home_goal_distance),
            )
            VerticalDivider(
                modifier = Modifier.height(20.dp),
                color = SixpackTheme.colors.gray100,
                thickness = 1.dp,
            )
            HomeTotalGoalInformationItem(
                modifier = Modifier,
                value = state.pace,
                title = stringResource(R.string.home_goal_distance),
            )
        }

        DoRunTotalGoalProgress(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            current = state.safeCurrentSessionCount,
            total = state.safeTotalSessionCount,
            progress = state.sessionProgress,
        )

        if (totalGoalCompleted) {
            HomeTotalGoalCompletedSubComponent(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
            )
        }
    }
}

@Preview
@Composable
private fun HomeTotalGoalComponentPreview() {
    DoRunPreviewWrapper {
        HomeTotalGoalComponent(
            state =
                HomeTotalGoalComponentState(
                    title = "이번 달 목표",
                    imageRes = R.drawable.ill_marathon_10km,
                    distance = "5.0km",
                    duration = "01:00:00",
                    pace = "6'00\"",
                    totalSessionCount = 20,
                    completedSessionCount = 4,
                ),
        )
    }
}

@Preview
@Composable
private fun HomeTotalGoalComponentCompletedPreview() {
    DoRunPreviewWrapper {
        HomeTotalGoalComponent(
            totalGoalCompleted = true,
            state =
                HomeTotalGoalComponentState(
                    title = "이번 달 목표",
                    imageRes = R.drawable.ill_marathon_10km,
                    distance = "5.0km",
                    duration = "01:00:00",
                    pace = "6'00\"",
                    totalSessionCount = 20,
                    completedSessionCount = 4,
                ),
        )
    }
}
