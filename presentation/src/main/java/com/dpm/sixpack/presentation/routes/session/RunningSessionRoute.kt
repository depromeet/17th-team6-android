package com.dpm.sixpack.presentation.routes.session

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.routes.session.component.ScreenSelectionTab
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionIntent
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionSideEffect
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningScreenTabItem
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionState
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber

@Composable
fun RunningSessionRoute(
    modifier: Modifier = Modifier,
    viewModel: RunningSessionViewModel = hiltViewModel(),
) {
    val tabItems = RunningScreenTabItem.entries
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabItems.size })

    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        // Collect
        when (sideEffect) {
            is RunningSessionSideEffect.NavigateBackToHome -> {
            }

            is RunningSessionSideEffect.NavigateToReport -> {
            }

            is RunningSessionSideEffect.ChangeTab -> {
                when (sideEffect.tab) {
                    RunningScreenTabItem.GOAL -> {
                        pagerState.animateScrollToPage(0)
                    }

                    RunningScreenTabItem.MAP -> {
                        pagerState.animateScrollToPage(1)
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = pagerState.currentPage != 1,
        ) { page ->

            when (page) {
                0 -> {
                    val sessionState = uiState.sessionState
                    if (sessionState is RunningSessionState.Initial) {
                        RunningGoalSubRoute(
                            goalUiState = sessionState.goal,
                        )
                    }
                }

                1 -> {
                    // do nothing
                }
            }
        }

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    // 목표 탭에서 지도 터치되는것 막기 위함
                    .pointerInput(pagerState.currentPage) {
                        if (pagerState.currentPage != 1) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    event.changes.forEach { it.consume() }
                                }
                            }
                        }
                    },
        ) {
            RunningSessionSubRoute(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = if (pagerState.currentPage == 1) 1f else 0f
                        },
                viewModel = viewModel,
            )
        }

        if (uiState.sessionState is RunningSessionState.Initial) {
            ScreenSelectionTab(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .windowInsetsPadding(insets = WindowInsets.statusBars)
                        .padding(horizontal = 48.dp),
                tabItems = tabItems,
                selectedIndex = pagerState.currentPage,
                onSelectionChange = { tab ->
                    viewModel.onIntent(RunningSessionIntent.TabChange(tab))
                },
            )

            InitialContent(
                onStartClick = {
                    if (pagerState.currentPage == 0) {
                        // 목표 -> 지도 이동 후 러닝 시작
                        viewModel.onIntent(RunningSessionIntent.TabChange(RunningScreenTabItem.MAP))
                        viewModel.onIntent(RunningSessionIntent.SessionStart)
                    } else {
                        viewModel.onIntent(RunningSessionIntent.SessionStart)
                    }
                },
            )
        }
    }
}

@Composable
private fun InitialContent(onStartClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        DoRunDefaultButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            onClick = {
                Timber.d("Running Session Start Clicked")
                onStartClick()
            },
            text = stringResource(id = R.string.session_start_running_button),
        )
    }
}

@Preview
@Composable
fun PreviewRunningRoute() {
    RunningSessionRoute()
}
