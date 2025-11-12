package com.dpm.sixpack.presentation.common.components.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.dpm.sixpack.presentation.routes.mypage.contract.GridItemType
import com.dpm.sixpack.presentation.routes.mypage.ui.component.EmptyState
import com.dpm.sixpack.presentation.routes.mypage.ui.component.ErrorState
import com.dpm.sixpack.presentation.routes.mypage.ui.component.PostGrid
import com.dpm.sixpack.presentation.routes.mypage.ui.component.PostTabLoadingState
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 포스트 그리드 컨텐츠 (공통 컴포넌트)
 * 인증 피드 그리드를 표시하는 재사용 가능한 컴포넌트
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostGridContent(
    gridItemsPagingItems: LazyPagingItems<GridItemType>,
    onPostClick: (Long) -> Unit,
    onRetry: () -> Unit = { gridItemsPagingItems.retry() },
    modifier: Modifier = Modifier,
    emptyTitle: String = "아직 완료한 인증이 없어요...",
    emptyDescription: String = "러닝을 완료하면 인증할 수 있어요!",
) {
    val refreshLoadState = gridItemsPagingItems.loadState.refresh
    val pullRefreshState = rememberPullToRefreshState()

    // 첫 로딩인지 판단 (데이터가 없으면서 로딩 중)
    val isInitialLoading =
        refreshLoadState is LoadState.Loading &&
            gridItemsPagingItems.itemCount == 0

    // Pull-to-Refresh 로딩 중 (데이터가 있으면서 로딩 중)
    val isRefreshing =
        refreshLoadState is LoadState.Loading &&
            gridItemsPagingItems.itemCount > 0

    val isEmpty =
        refreshLoadState is LoadState.NotLoading &&
            gridItemsPagingItems.itemCount == 0

    Column(modifier = modifier) {
        when {
            // 첫 로딩일 때는 전체 화면 로딩 표시
            isInitialLoading -> {
                PostTabLoadingState(modifier = Modifier.fillMaxSize())
            }

            // 에러 발생 시
            refreshLoadState is LoadState.Error -> {
                ErrorState(
                    message = refreshLoadState.error.message ?: "알 수 없는 오류가 발생했습니다",
                    onRetry = onRetry,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            // 데이터가 비어있을 때
            isEmpty -> {
                // Empty State도 Pull-to-Refresh 가능하게
                PullToRefreshBox(
                    state = pullRefreshState,
                    isRefreshing = isRefreshing,
                    onRefresh = { gridItemsPagingItems.refresh() },
                    modifier = Modifier.fillMaxSize(),
                    indicator = {
                        Indicator(
                            state = pullRefreshState,
                            isRefreshing = isRefreshing,
                            modifier = Modifier.align(Alignment.TopCenter),
                            color = SixpackTheme.colors.blue600,
                            containerColor = Color.White,
                        )
                    },
                ) {
                    EmptyState(
                        title = emptyTitle,
                        description = emptyDescription,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            // 데이터가 있을 때 (Pull-to-Refresh 활성화)
            else -> {
                PullToRefreshBox(
                    state = pullRefreshState,
                    isRefreshing = isRefreshing,
                    onRefresh = { gridItemsPagingItems.refresh() },
                    modifier = Modifier.fillMaxSize(),
                    indicator = {
                        Indicator(
                            state = pullRefreshState,
                            isRefreshing = isRefreshing,
                            modifier = Modifier.align(Alignment.TopCenter),
                            color = SixpackTheme.colors.blue600,
                            containerColor = Color.White,
                        )
                    },
                ) {
                    PostGrid(
                        gridItemsPagingItems = gridItemsPagingItems,
                        onPostClick = onPostClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}
