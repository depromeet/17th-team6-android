package com.dpm.sixpack.presentation.routes.mypage.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.skeleton.ShimmerBox
import com.dpm.sixpack.presentation.common.util.compose.rememberAdaptiveGridMinSize

/**
 * Loading state for the feed tab showing a grid of shimmer skeleton items.
 *
 * @param modifier Modifier to be applied to the loading state
 * @param itemCount Number of skeleton items to display (default: 12)
 */
@Composable
internal fun FeedTabLoadingState(
    modifier: Modifier = Modifier,
    itemCount: Int = 36,
) {
    // 최소 3개 컬럼을 보장하는 adaptive 그리드 셀 크기 계산
    val adaptiveMinSize = rememberAdaptiveGridMinSize()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = adaptiveMinSize),
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(
            count = itemCount,
            key = { index -> "loading_$index" },
        ) { _ ->
            ShimmerBox(
                width = 109.dp,
                height = 109.dp,
                shape = RoundedCornerShape(8.dp),
            )
        }
    }
}

@Preview()
@Composable
private fun FeedTabLoadingStatePreview() {
    DoRunPreviewWrapper {
        FeedTabLoadingState()
    }
}
