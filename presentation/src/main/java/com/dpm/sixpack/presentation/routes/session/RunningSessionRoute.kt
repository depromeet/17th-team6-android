package com.dpm.sixpack.presentation.routes.session

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.routes.session.component.RunningScreenTabItems
import com.dpm.sixpack.presentation.routes.session.component.ScreenSelectionTab
import kotlinx.coroutines.launch

@Composable
fun RunningSessionRoute(modifier: Modifier = Modifier) {
    val tabItems = RunningScreenTabItems.entries
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabItems.size })
    val coroutineScope = rememberCoroutineScope()

    val (isTabVisible, setTabVisible) = remember { mutableStateOf(true) }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        RunningSessionScreen(
            modifier =
                Modifier
                    .fillMaxSize()
                    // 지도 탭일 때만 보이도록 alpha 값을 조절
                    // 탭 전환때마다 지도 로딩 반복하는 것 방지
                    .graphicsLayer {
                        alpha = if (pagerState.currentPage == 1) 1f else 0f
                    },
            // 러닝세션 시작하면 화면전환 Tab 사라짐
            onSessionStart = {
                setTabVisible(false)
            },
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            when (page) {
                0 -> {
                    RunningGoalScreen()
                }

                1 -> {
                    // 지도 탭일땐 배경 투명, RunningSessionScreen이 보이도록
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(Color.Transparent),
                    )
                }
            }
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
