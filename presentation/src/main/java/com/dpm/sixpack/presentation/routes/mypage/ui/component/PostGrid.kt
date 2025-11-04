package com.dpm.sixpack.presentation.routes.mypage.ui.component

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.dpm.sixpack.presentation.common.components.image.DoRunAsyncImage
import com.dpm.sixpack.presentation.routes.mypage.contract.GridItemType
import com.dpm.sixpack.presentation.routes.mypage.contract.Post
import com.dpm.sixpack.presentation.theme.SixpackTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
internal fun PostGrid(
    gridItemsPagingItems: LazyPagingItems<GridItemType>,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
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
                androidx.compose.foundation.lazy.grid.GridItemSpan(1)
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
                .background(SixpackTheme.colors.gray50)
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
    modifier: Modifier = Modifier,
) {
    val day =
        try {
            val dateTime = LocalDateTime.parse(post.createdAt, DateTimeFormatter.ISO_DATE_TIME)
            "${dateTime.dayOfMonth}일"
        } catch (_: Exception) {
            ""
        }

    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp)),
    ) {
        // Image
        DoRunAsyncImage(
            imageUrl = post.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
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
