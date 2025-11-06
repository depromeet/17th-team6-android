package com.dpm.sixpack.presentation.routes.mypage.ui.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.dpm.sixpack.presentation.routes.mypage.contract.GridItemType
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPagePostTabIntent
import com.dpm.sixpack.presentation.routes.mypage.ui.component.EmptyState
import com.dpm.sixpack.presentation.routes.mypage.ui.component.ErrorState
import com.dpm.sixpack.presentation.routes.mypage.ui.component.PostGrid
import com.dpm.sixpack.presentation.routes.mypage.ui.component.PostTabLoadingState

@Composable
internal fun PostTabContent(
    gridItemsPagingItems: LazyPagingItems<GridItemType>,
    onIntent: (MyPagePostTabIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val refreshLoadState = gridItemsPagingItems.loadState.refresh

    val isEmpty =
        refreshLoadState is LoadState.NotLoading &&
            gridItemsPagingItems.itemCount == 0

    Column(modifier = modifier) {
        when {
            refreshLoadState is LoadState.Loading -> {
                PostTabLoadingState(modifier = Modifier.fillMaxSize())
            }

            refreshLoadState is LoadState.Error -> {
                ErrorState(
                    message = refreshLoadState.error.message ?: "알 수 없는 오류가 발생했습니다",
                    onRetry = { gridItemsPagingItems.retry() },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            isEmpty -> {
                EmptyState(
                    title = "아직 완료한 인증이 없어요...",
                    description = "러닝을 완료하면 인증할 수 있어요!",
                    modifier = Modifier.fillMaxSize(),
                )
            }

            else -> {
                PostGrid(
                    gridItemsPagingItems = gridItemsPagingItems,
                    onPostClick = { postId ->
                        onIntent(MyPagePostTabIntent.OnPostClick(postId))
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
