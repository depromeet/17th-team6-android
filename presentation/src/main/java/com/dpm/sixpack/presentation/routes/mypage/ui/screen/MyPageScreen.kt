package com.dpm.sixpack.presentation.routes.mypage.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunTopBarSlot
import com.dpm.sixpack.presentation.routes.mypage.contract.CertificationStatus
import com.dpm.sixpack.presentation.routes.mypage.contract.GridItemType
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageIntent
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPagePostTabIntent
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageRecordTabIntent
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageRecordTabState
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageState
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageTab
import com.dpm.sixpack.presentation.routes.mypage.contract.Post
import com.dpm.sixpack.presentation.routes.mypage.contract.ProfileInfo
import com.dpm.sixpack.presentation.routes.mypage.contract.RecordItem
import com.dpm.sixpack.presentation.routes.mypage.ui.component.MyPageTabText
import com.dpm.sixpack.presentation.routes.mypage.ui.component.ProfileSection
import com.dpm.sixpack.presentation.routes.mypage.ui.content.PostTabContent
import com.dpm.sixpack.presentation.routes.mypage.ui.content.RecordTabContent
import com.dpm.sixpack.presentation.theme.SixpackTheme
import kotlinx.coroutines.flow.flowOf

@Composable
fun MyPageScreen(
    state: MyPageState,
    recordTabState: MyPageRecordTabState,
    gridItemsPagingItems: LazyPagingItems<GridItemType>,
    onIntent: (MyPageIntent) -> Unit,
    onPostTabIntent: (MyPagePostTabIntent) -> Unit,
    onRecordTabIntent: (MyPageRecordTabIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(initialPage = state.selectedTab.ordinal) { 2 }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val tab = MyPageTab.entries[page]
            onIntent(MyPageIntent.OnTabClick(tab))
        }
    }

    LaunchedEffect(state.selectedTab) {
        if (pagerState.currentPage != state.selectedTab.ordinal) {
            pagerState.animateScrollToPage(state.selectedTab.ordinal)
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(SixpackTheme.colors.gray0),
    ) {
        // TopBar
        MyPageTopBar(
            onSettingClick = { onIntent(MyPageIntent.OnSettingClick) },
        )

        // Profile Section
        ProfileSection(
            profileInfo = state.profileInfo,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        )

        // Tab Row
        TabRow(
            selectedTabIndex = state.selectedTab.ordinal,
            modifier = Modifier.fillMaxWidth(),
            containerColor = SixpackTheme.colors.gray0,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[state.selectedTab.ordinal]),
                    height = 2.dp,
                    color = SixpackTheme.colors.gray800,
                )
            },
            divider = {
                HorizontalDivider(color = SixpackTheme.colors.gray100, thickness = 1.dp)
            },
        ) {
            Tab(
                selected = state.selectedTab == MyPageTab.CERTIFICATION,
                onClick = { onIntent(MyPageIntent.OnTabClick(MyPageTab.CERTIFICATION)) },
                text = {
                    MyPageTabText(
                        text = "인증",
                        isSelected = state.selectedTab == MyPageTab.CERTIFICATION,
                    )
                },
            )
            Tab(
                selected = state.selectedTab == MyPageTab.RECORD,
                onClick = { onIntent(MyPageIntent.OnTabClick(MyPageTab.RECORD)) },
                text = {
                    MyPageTabText(
                        text = "기록",
                        isSelected = state.selectedTab == MyPageTab.RECORD,
                    )
                },
            )
        }

        // Pager Content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            when (page) {
                0 ->
                    PostTabContent(
                        gridItemsPagingItems = gridItemsPagingItems,
                        onIntent = onPostTabIntent,
                        modifier = Modifier.fillMaxSize(),
                    )

                1 ->
                    RecordTabContent(
                        state = recordTabState,
                        onIntent = onRecordTabIntent,
                        modifier = Modifier.fillMaxSize(),
                    )
            }
        }
    }
}

@Composable
private fun MyPageTopBar(
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DoRunTopBarSlot(
        modifier = modifier,
        leadingContent = {
            Text(
                text = "마이",
                style = SixpackTheme.typography.t1Bold,
                color = SixpackTheme.colors.gray900,
            )
        },
        trailingContent = {
            IconButton(onClick = onSettingClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_settings),
                    contentDescription = "설정",
                    tint = SixpackTheme.colors.gray800,
                    modifier = Modifier.size(24.dp),
                )
            }
        },
    )
}

@Preview
@Composable
private fun MyPageScreenPreview() {
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

        MyPageScreen(
            state =
                MyPageState(
                    selectedTab = MyPageTab.CERTIFICATION,
                    profileInfo =
                        ProfileInfo(
                            nickname = "두런두런",
                            profileImageUrl = null,
                            friendCount = 7,
                            totalDistanceKm = 400.0,
                            certificationCount = 120,
                        ),
                ),
            recordTabState = MyPageRecordTabState(),
            gridItemsPagingItems = gridItemsPagingItems,
            onIntent = {},
            onPostTabIntent = {},
            onRecordTabIntent = {},
        )
    }
}

@Preview
@Composable
private fun MyPageScreenRecordTabPreview() {
    DoRunPreviewWrapper {
        val gridItemsPagingItems = flowOf(PagingData.from(emptyList<GridItemType>())).collectAsLazyPagingItems()

        MyPageScreen(
            state =
                MyPageState(
                    selectedTab = MyPageTab.RECORD,
                    profileInfo =
                        ProfileInfo(
                            nickname = "두런두런",
                            profileImageUrl = null,
                            friendCount = 7,
                            totalDistanceKm = 400.0,
                            certificationCount = 120,
                        ),
                ),
            recordTabState =
                MyPageRecordTabState(
                    records =
                        listOf(
                            RecordItem(
                                id = 1,
                                date = "2025.09.30 (화)",
                                time = "오전 10:11",
                                distanceKm = 8.02,
                                durationFormatted = "01:12:03",
                                paceFormatted = "6'74\"",
                                cadence = 128,
                                certificationStatus = CertificationStatus.COMPLETED,
                            ),
                        ),
                ),
            gridItemsPagingItems = gridItemsPagingItems,
            onIntent = {},
            onPostTabIntent = {},
            onRecordTabIntent = {},
        )
    }
}

@Preview
@Composable
private fun MyPageScreenEmptyPreview() {
    DoRunPreviewWrapper {
        val gridItemsPagingItems = flowOf(PagingData.from(emptyList<GridItemType>())).collectAsLazyPagingItems()

        MyPageScreen(
            state =
                MyPageState(
                    profileInfo =
                        ProfileInfo(
                            nickname = "두런두런",
                            profileImageUrl = null,
                            friendCount = 7,
                            totalDistanceKm = 0.0,
                            certificationCount = 0,
                        ),
                ),
            recordTabState = MyPageRecordTabState(),
            gridItemsPagingItems = gridItemsPagingItems,
            onIntent = {},
            onPostTabIntent = {},
            onRecordTabIntent = {},
        )
    }
}
