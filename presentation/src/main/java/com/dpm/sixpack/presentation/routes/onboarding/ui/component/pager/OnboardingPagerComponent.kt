package com.dpm.sixpack.presentation.routes.onboarding.ui.component.pager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.routes.onboarding.ui.component.pager.item.OnboardingPagerIndicator
import com.dpm.sixpack.presentation.routes.onboarding.ui.component.pager.item.OnboardingPagerItem
import com.dpm.sixpack.presentation.routes.onboarding.ui.model.OnboardingPage
import com.dpm.sixpack.presentation.routes.onboarding.util.OnboardingConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OnboardingPagerComponent(
    modifier: Modifier = Modifier,
    pages: List<OnboardingPage> = OnboardingPage.entries.toList(),
) {
    val realPageCount = Int.MAX_VALUE
    val logicalPageCount by remember(pages) {
        derivedStateOf {
            pages.size
        }
    }
    val initialPage by remember(logicalPageCount) {
        derivedStateOf {
            val center = realPageCount / 2
            val offset = center % logicalPageCount
            center - offset
        }
    }

    val pagerState =
        rememberPagerState(
            initialPage = initialPage,
            pageCount = { realPageCount },
        )

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = pagerState.currentPage) {
        while (true) {
            delay(OnboardingConstants.AUTO_PAGE_SCROLLING_INTERVAL)
            coroutineScope.launch {
                pagerState.animateScrollToPage(
                    page = (pagerState.currentPage + 1) % pagerState.pageCount,
                    animationSpec =
                        androidx.compose.animation.core
                            .tween(700),
                )
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            OnboardingPagerItem(
                modifier = Modifier.padding(bottom = 40.dp),
                page = pages[page % logicalPageCount],
            )
        }
        OnboardingPagerIndicator(
            size = logicalPageCount,
            currentPage = pagerState.currentPage % logicalPageCount,
        )
    }
}
