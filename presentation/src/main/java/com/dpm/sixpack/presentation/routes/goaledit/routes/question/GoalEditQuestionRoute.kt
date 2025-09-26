package com.dpm.sixpack.presentation.routes.goaledit.routes.question

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun GoalEditQuestionRoute(
    modifier: Modifier = Modifier,
    viewModel: GoalEditQuestionViewModel = hiltViewModel(),
    onNavigateToBack: () -> Unit = {},
    onNavigateToGoalEditResult: () -> Unit = {},
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier =
            modifier
                .fillMaxSize(),
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
                    IconButton(onClick = onNavigateToBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_left),
                            contentDescription = "뒤로가기", // TODO: 접근성 resource 추가
                            tint = SixpackTheme.colors.gray800,
                        )
                    }
                },
            )
        },
    ) { paddingValues ->

        Box(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            Column(
                modifier =
                    modifier
                        .fillMaxSize()
                        .scrollable(
                            state = scrollState,
                            orientation = Orientation.Vertical,
                        ).padding(bottom = 92.dp),
            ) {
            }

            DoRunDefaultButton(
                modifier =
                    Modifier
                        .padding(top = 16.dp, bottom = 24.dp)
                        .padding(horizontal = 16.dp)
                        .align(Alignment.BottomCenter),
                text = stringResource(R.string.common_next),
                onClick = onNavigateToGoalEditResult,
            )
        }
    }
}
