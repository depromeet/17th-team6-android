package com.dpm.sixpack.presentation.routes.mypage.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun RecordTabLoadingState(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        // Month Navigation - 실제와 동일하게 표시
        MonthNavigation(
            yearMonth = com.dpm.sixpack.presentation.routes.mypage.contract.YearMonth(),
            onPreviousClick = {},
            onNextClick = {},
            canGoPrevious = false,
            canGoNext = false,
        )

        // Skeleton Cards
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(36.dp),
        ) {
            items(3) { groupIndex ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Date header skeleton
                    androidx.compose.foundation.layout.Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(21.dp)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                                .background(SixpackTheme.colors.gray200),
                    )

                    // Record cards skeleton
                    repeat(if (groupIndex == 0) 2 else 1) {
                        RecordCardSkeleton()
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun RecordTabLoadingStatePreview() {
    DoRunPreviewWrapper {
        RecordTabLoadingState()
    }
}

