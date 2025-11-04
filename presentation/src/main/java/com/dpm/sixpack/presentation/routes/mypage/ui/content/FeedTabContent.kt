package com.dpm.sixpack.presentation.routes.mypage.ui.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.dpm.sixpack.presentation.routes.mypage.contract.GridItemType
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageFeedTabIntent
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageFeedTabState
import com.dpm.sixpack.presentation.routes.mypage.ui.component.EmptyState
import com.dpm.sixpack.presentation.routes.mypage.ui.component.PostGrid

@Composable
internal fun FeedTabContent(
    state: MyPageFeedTabState,
    gridItemsPagingItems: LazyPagingItems<GridItemType>,
    onIntent: (MyPageFeedTabIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEmpty =
        gridItemsPagingItems.loadState.refresh is LoadState.NotLoading &&
            gridItemsPagingItems.itemCount == 0

    Column(modifier = modifier) {
        if (isEmpty) {
            EmptyState(
                title = "아직 완료한 인증이 없어요...",
                description = "러닝을 완료하면 인증할 수 있어요!",
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            PostGrid(
                gridItemsPagingItems = gridItemsPagingItems,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
