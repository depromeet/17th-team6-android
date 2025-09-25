package com.dpm.sixpack.presentation.routes.sessionlist.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.sessionlist.contract.SessionListItemState
import com.dpm.sixpack.presentation.routes.sessionlist.contract.SessionListScreenState
import com.dpm.sixpack.presentation.routes.sessionlist.contract.SessionListTotalGoalComponentState
import com.dpm.sixpack.presentation.routes.sessionlist.ui.component.SessionListErrorSnackBar
import com.dpm.sixpack.presentation.routes.sessionlist.ui.component.SessionListItem
import com.dpm.sixpack.presentation.routes.sessionlist.ui.component.SessionListTotalGoalComponent
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun SessionListScreen(
    screenState: SessionListScreenState,
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onClickBackNavigation: () -> Unit = {},
    onClickEditGoal: () -> Unit = {},
    onClickSessionItem: (sessionId: Long) -> Unit = {},
    onClickStartPreviousSession: (sessionId: Long) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        containerColor = SixpackTheme.colors.gray0,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.home_goal_list_title),
                        style = SixpackTheme.typography.t2Bold,
                        color = SixpackTheme.colors.gray900,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClickBackNavigation) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_left),
                            contentDescription = "뒤로가기", // TODO: 접근성 resource 추가
                            tint = SixpackTheme.colors.gray800,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults
                        .topAppBarColors()
                        .copy(containerColor = SixpackTheme.colors.gray0),
            )
        },
        snackbarHost = {
            androidx.compose.material3.SnackbarHost(
                hostState = snackBarHostState,
                snackbar = {
                    SessionListErrorSnackBar(
                        modifier = Modifier.padding(bottom = 190.dp),
                        iconRes = R.drawable.ill_warning,
                        title = it.visuals.message,
                    )
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .padding(top = 24.dp)
                    .padding(horizontal = 20.dp),
        ) {
            SessionListTotalGoalComponent(
                state = screenState.totalGoalComponentState,
                onClickEdit = onClickEditGoal,
            )

            LazyColumn(
                modifier = Modifier,
                contentPadding = PaddingValues(top = 32.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    screenState.sessionList,
                ) { item ->
                    SessionListItem(
                        modifier = Modifier,
                        state = item,
                        onClickContainer = {
                            onClickSessionItem(item.id)
                        },
                        onClickStartPreviousSession = {
                            onClickStartPreviousSession(item.id)
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SessionListScreenPreview() {
    val completedSessionCount = 3
    val mockState =
        SessionListScreenState(
            totalGoalComponentState =
                SessionListTotalGoalComponentState(
                    title = "러닝 초보 탈출을 위한 5km 달리기",
                    imageRes = R.drawable.ill_marathon_42km,
                    completedSessionCount = completedSessionCount,
                    totalSessionCount = 15,
                ),
            sessionList =
                List(10) { index ->
                    SessionListItemState(
                        id = index.toLong(),
                        roundCount = index + 1,
                        distance = "5km",
                        duration = "1:12:03",
                        pace = "6'00\"/km",
                        isCompleted = index < completedSessionCount,
                        isSelected = index == completedSessionCount - 1,
                    )
                },
        )

    DoRunPreviewWrapper {
        SessionListScreen(
            screenState = mockState,
        )
    }
}
