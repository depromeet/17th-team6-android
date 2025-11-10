package com.dpm.sixpack.presentation.routes.mypage.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.dpm.sixpack.core.util.TimeUtil
import com.dpm.sixpack.presentation.common.components.image.DoRunAsyncImage
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.util.compose.rememberAdaptiveGridMinSize
import com.dpm.sixpack.presentation.routes.mypage.contract.GridItemType
import com.dpm.sixpack.presentation.routes.mypage.contract.Post
import com.dpm.sixpack.presentation.theme.SixpackTheme
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun PostGrid(
    gridItemsPagingItems: LazyPagingItems<GridItemType>,
    onPostClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
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
            count = gridItemsPagingItems.itemCount,
            key = { index ->
                when (val item = gridItemsPagingItems[index]) {
                    is GridItemType.MonthLabel -> "month_${item.year}_${item.month}"
                    is GridItemType.PostItem -> "post_${item.post.id}"
                    null -> "loading_$index"
                }
            },
            span = { index ->
                GridItemSpan(1)
            },
        ) { index ->
            when (val item = gridItemsPagingItems[index]) {
                is GridItemType.MonthLabel -> {
                    MonthGridItem(
                        year = item.year,
                        month = item.month,
                        modifier = Modifier.height(109.dp),
                    )
                }

                is GridItemType.PostItem -> {
                    PostGridItem(
                        post = item.post,
                        onClick = { onPostClick(item.post.id) },
                        modifier = Modifier.size(109.dp),
                    )
                }

                null -> {
                    // Loading placeholder
                    Box(
                        modifier =
                            Modifier
                                .size(109.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SixpackTheme.colors.gray100),
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthGridItem(
    year: Int,
    month: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Transparent)
                .padding(horizontal = 24.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = year.toString(),
                style = SixpackTheme.typography.b2Regular,
                color = SixpackTheme.colors.gray700,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "${month}월",
                style = SixpackTheme.typography.h1Medium,
                color = SixpackTheme.colors.gray900,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun PostGridItem(
    post: Post,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // TimeUtil의 공통 날짜 파싱 함수 사용
    val day =
        TimeUtil.parseToLocalDateTime(post.createdAt)?.let { dateTime ->
            "${dateTime.dayOfMonth}일"
        } ?: ""

    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick),
    ) {
        // Image
        DoRunAsyncImage(
            imageUrl = post.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )

        // Dim layer
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(SixpackTheme.colors.gray900.copy(alpha = 0.3f)),
        )

        // Day label overlay
        if (day.isNotEmpty()) {
            Text(
                text = day,
                style = SixpackTheme.typography.t1Medium,
                color = SixpackTheme.colors.gray0,
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .padding(8.dp),
            )
        }
    }
}

@Preview
@Composable
private fun PostGridPreview() {
    DoRunPreviewWrapper {
        val mockGridItems =
            listOf(
                GridItemType.MonthLabel(year = 2025, month = 10),
                GridItemType.PostItem(Post(id = 1, imageUrl = null, createdAt = "2025-10-14T10:30:00Z")),
                GridItemType.PostItem(Post(id = 2, imageUrl = null, createdAt = "2025-10-12T15:20:00Z")),
                GridItemType.PostItem(Post(id = 3, imageUrl = null, createdAt = "2025-10-09T08:45:00Z")),
                GridItemType.MonthLabel(year = 2025, month = 9),
                GridItemType.PostItem(Post(id = 4, imageUrl = null, createdAt = "2025-09-30T14:30:00Z")),
                GridItemType.PostItem(Post(id = 5, imageUrl = null, createdAt = "2025-09-28T12:00:00Z")),
            )
        val gridItemsPagingItems = flowOf(PagingData.from(mockGridItems)).collectAsLazyPagingItems()

        PostGrid(
            gridItemsPagingItems = gridItemsPagingItems,
            onPostClick = {},
        )
    }
}
