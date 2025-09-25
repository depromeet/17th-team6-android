package com.dpm.sixpack.presentation.routes.session_list.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.session_list.contract.SessionListItemState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun SessionListItem(
    state: SessionListItemState,
    modifier: Modifier = Modifier,
    onClickContainer: () -> Unit = {},
    onClickStartPreviousSession: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (state.isSelected) {
                    SixpackTheme.colors.blue600
                } else {
                    SixpackTheme.colors.gray100
                },
                shape = SixpackTheme.shapes.round16
            )
            .clip(SixpackTheme.shapes.round16)
            .clickable(onClick = onClickContainer)
            .padding(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = state.title,
                    style = SixpackTheme.typography.t2Bold,
                    color = SixpackTheme.colors.gray900,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.Start
                    )
                ) {
                    Text(
                        text = state.distance,
                        style = SixpackTheme.typography.b1Regular,
                        color = SixpackTheme.colors.gray600
                    )
                    VerticalDivider(
                        modifier = Modifier.height(14.dp),
                        thickness = 1.dp,
                        color = SixpackTheme.colors.gray100
                    )
                    Text(
                        text = state.duration,
                        style = SixpackTheme.typography.b1Regular,
                        color = SixpackTheme.colors.gray600
                    )
                    VerticalDivider(
                        modifier = Modifier.height(14.dp),
                        thickness = 1.dp,
                        color = SixpackTheme.colors.gray100
                    )
                    Text(
                        text = state.pace,
                        style = SixpackTheme.typography.b1Regular,
                        color = SixpackTheme.colors.gray600,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Image(
                modifier = Modifier.padding(start = 8.dp),
                painter = painterResource(
                    if (state.isCompleted) {
                        R.drawable.ill_session_completed
                    } else {
                        R.drawable.ill_session_uncompleted
                    }
                ),
                contentDescription = "세션 완료 일러스트", // TODO: 접근성 resource 추가
            )
        }

        if (state.showButton) {
            DoRunDefaultButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                text = stringResource(R.string.home_goal_list_run_again),
                onClick = onClickStartPreviousSession
            )
        }
    }
}


private val mockState = SessionListItemState(
    sessionId = 1,
    title = "러닝 초보 탈출을 위한 5km 달리기러닝 초보 탈출을 위한 5km 달리기",
    distance = "5km",
    duration = "1:12:03",
    pace = "6'00\"/km",
    isCompleted = true,
    isSelected = true,
)

@Preview
@Composable
private fun SessionListItemPreview1() {
    DoRunPreviewWrapper {
        SessionListItem(
            state = mockState
        )
    }
}

@Preview
@Composable
private fun SessionListItemPreview2() {
    DoRunPreviewWrapper {
        SessionListItem(
            state = mockState.copy(isSelected = false)
        )
    }
}

@Preview
@Composable
private fun SessionListItemPreview3() {
    DoRunPreviewWrapper {
        SessionListItem(
            state = mockState.copy(isCompleted = false, isSelected = false)
        )
    }
}



