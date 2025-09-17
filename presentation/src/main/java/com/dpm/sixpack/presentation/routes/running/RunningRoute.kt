package com.dpm.sixpack.presentation.routes.running

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.routes.running.component.ScreenSelectionTab
import kotlinx.coroutines.launch

@Composable
fun RunningRoute(modifier: Modifier = Modifier) {
    val tabItems = listOf("목표", "지도")
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { tabItems.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            when (page) {
                0 -> {
                    RunningGoalScreen()
                }

                1 -> {
                    RunningSessionScreen()
                }
            }
        }

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

@Preview
@Composable
fun PreviewRunningRoute() {
    RunningRoute()
}
