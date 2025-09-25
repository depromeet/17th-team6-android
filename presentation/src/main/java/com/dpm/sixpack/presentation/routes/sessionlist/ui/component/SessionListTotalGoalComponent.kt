package com.dpm.sixpack.presentation.routes.sessionlist.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunTotalGoalProgress
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.sessionlist.contract.SessionListTotalGoalComponentState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun SessionListTotalGoalComponent(
    state: SessionListTotalGoalComponentState,
    modifier: Modifier = Modifier,
    onClickEdit: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .background(
                    color = SixpackTheme.colors.blue100,
                    shape = SixpackTheme.shapes.round16,
                ).padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Icon(
                modifier =
                    Modifier
                        .size(32.dp)
                        .clickable(onClick = onClickEdit)
                        .padding(4.dp),
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = "운동 목표 편집 아이콘", // TODO: 접근성 resource 추가
                tint = SixpackTheme.colors.gray800,
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
    }
}

@Preview
@Composable
private fun SessionListTotalGoalComponentPreview() {
    DoRunPreviewWrapper {
        SessionListTotalGoalComponent(
            state =
                SessionListTotalGoalComponentState(
                    title = "초보자 러닝 목표",
                    imageRes = R.drawable.ill_marathon_10km,
                    completedSessionCount = 3,
                    totalSessionCount = 8,
                ),
        )
    }
}
