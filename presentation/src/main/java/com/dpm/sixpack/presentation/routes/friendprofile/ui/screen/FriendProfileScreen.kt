package com.dpm.sixpack.presentation.routes.friendprofile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.dpm.sixpack.presentation.common.components.dialog.DoRunErrorScreen
import com.dpm.sixpack.presentation.common.components.post.PostGridContent
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.routes.friendprofile.contract.FriendProfileIntent
import com.dpm.sixpack.presentation.routes.friendprofile.contract.FriendProfileState
import com.dpm.sixpack.presentation.routes.mypage.contract.GridItemType
import com.dpm.sixpack.presentation.routes.mypage.contract.Post
import com.dpm.sixpack.presentation.routes.mypage.contract.ProfileInfo
import com.dpm.sixpack.presentation.routes.mypage.ui.component.ProfileSection
import com.dpm.sixpack.presentation.theme.SixpackTheme
import kotlinx.coroutines.flow.flowOf

@Composable
fun FriendProfileScreen(
    state: FriendProfileState,
    gridItemsPagingItems: LazyPagingItems<GridItemType>,
    onIntent: (FriendProfileIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 에러가 있는 경우 에러 화면 표시
    if (state.error != null) {
        DoRunErrorScreen(
            modifier = modifier.fillMaxSize(),
            title = state.error.title,
            description = state.error.description,
            confirmButtonText = "다시 시도",
            onConfirmClick = { onIntent(FriendProfileIntent.OnRetryClick) },
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DoRunNavigationTopBar(
                navigateToBack = { onIntent(FriendProfileIntent.OnBackClick) },
            )
        },
        containerColor = SixpackTheme.colors.gray0,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            // Profile Section
            ProfileSection(
                profileInfo = state.profileInfo,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            )

            // Divider
            HorizontalDivider(
                color = SixpackTheme.colors.gray100,
                thickness = 1.dp,
            )

            // Post Grid Content
            PostGridContent(
                gridItemsPagingItems = gridItemsPagingItems,
                onPostClick = { postId ->
                    onIntent(FriendProfileIntent.OnPostClick(postId))
                },
                onRetry = {
                    onIntent(FriendProfileIntent.OnRetryClick)
                    gridItemsPagingItems.retry()
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview
@Composable
private fun FriendProfileScreenPreview() {
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
                GridItemType.PostItem(Post(id = 6, imageUrl = null, createdAt = "2025-09-25T14:30:00Z")),
            )
        val gridItemsPagingItems = flowOf(PagingData.from(mockGridItems)).collectAsLazyPagingItems()

        FriendProfileScreen(
            state =
                FriendProfileState(
                    profileInfo =
                        ProfileInfo(
                            nickname = "두런두런",
                            profileImageUrl = null,
                            friendCount = 7,
                            totalDistanceKm = 400.0,
                            certificationCount = 120,
                        ),
                ),
            gridItemsPagingItems = gridItemsPagingItems,
            onIntent = {},
        )
    }
}
