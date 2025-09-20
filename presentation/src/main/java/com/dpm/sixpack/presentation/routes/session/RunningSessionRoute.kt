package com.dpm.sixpack.presentation.routes.session

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.routes.session.component.ScreenSelectionTab
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningScreenTabItem
import kotlinx.coroutines.launch

@Composable
fun RunningSessionRoute(modifier: Modifier = Modifier) {
    val tabItems = RunningScreenTabItem.entries
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabItems.size })
    val coroutineScope = rememberCoroutineScope()

    val (isTabVisible, setTabVisible) = remember { mutableStateOf(true) }

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
                    RunningGoalScreen()
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
            RunningSessionScreen(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = if (pagerState.currentPage == 1) 1f else 0f
                        },
                onSessionStart = {
                    setTabVisible(false)
                },
            )
        }

        if (isTabVisible) {
            ScreenSelectionTab(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .windowInsetsPadding(insets = WindowInsets.statusBars)
                        .padding(horizontal = 48.dp),
                items = tabItems,
                selectedIndex = pagerState.currentPage,
                onSelectionChange = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            )
        }
    }
}

@Preview
@Composable
fun PreviewRunningRoute() {
    RunningSessionRoute()
}
