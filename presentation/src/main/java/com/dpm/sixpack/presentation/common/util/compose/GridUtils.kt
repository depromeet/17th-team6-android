package com.dpm.sixpack.presentation.common.util.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

/**
 * 최소 컬럼 개수를 보장하는 adaptive 그리드 셀의 최소 크기를 계산합니다.
 *
 * @param minColumns 최소 컬럼 개수 (기본값: 3)
 * @param desiredItemSize 원하는 아이템 크기 (기본값: 109.dp)
 * @param horizontalPadding 그리드의 좌우 패딩 합계 (기본값: 40.dp)
 * @param itemSpacing 아이템 간 간격 (기본값: 4.dp)
 * @return 계산된 adaptive 최소 크기
 */
@Composable
internal fun rememberAdaptiveGridMinSize(
    minColumns: Int = 3,
    desiredItemSize: Dp = 109.dp,
    horizontalPadding: Dp = 40.dp,
    itemSpacing: Dp = 4.dp,
): Dp {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    return remember(screenWidth, minColumns, desiredItemSize, horizontalPadding, itemSpacing) {
        val totalSpacing = itemSpacing * (minColumns - 1)
        val maxItemSize = (screenWidth - horizontalPadding - totalSpacing) / minColumns
        min(desiredItemSize.value, maxItemSize.value).dp
    }
}
