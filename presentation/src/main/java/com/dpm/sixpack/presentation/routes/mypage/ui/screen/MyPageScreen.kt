package com.dpm.sixpack.presentation.routes.mypage.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.image.DoRunAsyncImage
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunTopBarSlot
import com.dpm.sixpack.presentation.routes.mypage.contract.CertificationStatus
import com.dpm.sixpack.presentation.routes.mypage.contract.GridItemType
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageIntent
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageState
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageTab
import com.dpm.sixpack.presentation.routes.mypage.contract.Post
import com.dpm.sixpack.presentation.routes.mypage.contract.ProfileInfo
import com.dpm.sixpack.presentation.routes.mypage.contract.RecordItem
import com.dpm.sixpack.presentation.routes.mypage.contract.YearMonth
import com.dpm.sixpack.presentation.theme.SixpackTheme
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MyPageScreen(
    state: MyPageState,
    gridItemsPagingItems: LazyPagingItems<GridItemType>,
    onIntent: (MyPageIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(initialPage = state.selectedTab.ordinal) { 2 }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val tab = MyPageTab.entries[page]
            if (tab != state.selectedTab) {
                onIntent(MyPageIntent.OnTabClick(tab))
            }
        }
    }

    LaunchedEffect(state.selectedTab) {
        if (pagerState.currentPage != state.selectedTab.ordinal) {
            pagerState.animateScrollToPage(state.selectedTab.ordinal)
        }
    }

    Column(
        modifier = modifier
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
                    color = SixpackTheme.colors.gray900,
                )
            },
            divider = {
                HorizontalDivider(color = SixpackTheme.colors.gray200, thickness = 1.dp)
            },
        ) {
            Tab(
                selected = state.selectedTab == MyPageTab.CERTIFICATION,
                onClick = { onIntent(MyPageIntent.OnTabClick(MyPageTab.CERTIFICATION)) },
                text = {
                    Text(
                        text = "인증",
                        style =
                            if (state.selectedTab == MyPageTab.CERTIFICATION) {
                                SixpackTheme.typography.b1Bold
                            } else {
                                SixpackTheme.typography.b1Regular
                            },
                        color =
                            if (state.selectedTab == MyPageTab.CERTIFICATION) {
                                SixpackTheme.colors.gray900
                            } else {
                                SixpackTheme.colors.gray500
                            },
                    )
                },
            )
            Tab(
                selected = state.selectedTab == MyPageTab.RECORD,
                onClick = { onIntent(MyPageIntent.OnTabClick(MyPageTab.RECORD)) },
                text = {
                    Text(
                        text = "기록",
                        style =
                            if (state.selectedTab == MyPageTab.RECORD) {
                                SixpackTheme.typography.b1Bold
                            } else {
                                SixpackTheme.typography.b1Regular
                            },
                        color =
                            if (state.selectedTab == MyPageTab.RECORD) {
                                SixpackTheme.colors.gray900
                            } else {
                                SixpackTheme.colors.gray500
                            },
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
                    CertificationTabContent(
                        currentYearMonth = state.currentYearMonth,
                        gridItemsPagingItems = gridItemsPagingItems,
                        onIntent = onIntent,
                        modifier = Modifier.fillMaxSize(),
                    )
                1 ->
                    RecordTabContent(
                        currentYearMonth = state.currentYearMonth,
                        records = state.records,
                        onIntent = onIntent,
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

@Composable
private fun ProfileSection(
    profileInfo: ProfileInfo,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Profile Image + Name + Friends
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Profile Image
            Box(
                modifier =
                    Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(SixpackTheme.colors.gray200),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_profile),
                    contentDescription = null,
                    tint = SixpackTheme.colors.gray600,
                    modifier = Modifier.size(24.dp),
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = profileInfo.nickname,
                    style = SixpackTheme.typography.t1Bold,
                    color = SixpackTheme.colors.gray900,
                )
                Text(
                    text = "친구 ${profileInfo.friendCount}명",
                    style = SixpackTheme.typography.c1Regular,
                    color = SixpackTheme.colors.gray600,
                )
            }
        }

        // Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            StatItem(
                label = "누적 거리",
                value = "${profileInfo.totalDistanceKm}km",
                modifier = Modifier.weight(1f),
            )
            StatItem(
                label = "인증 횟수",
                value = "${profileInfo.certificationCount}회",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = label,
            style = SixpackTheme.typography.c1Regular,
            color = SixpackTheme.colors.gray600,
        )
        Text(
            text = value,
            style = SixpackTheme.typography.b1Bold,
            color = SixpackTheme.colors.gray900,
        )
    }
}

@Composable
private fun CertificationTabContent(
    currentYearMonth: YearMonth,
    gridItemsPagingItems: LazyPagingItems<GridItemType>,
    onIntent: (MyPageIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEmpty =
        gridItemsPagingItems.loadState.refresh is LoadState.NotLoading &&
            gridItemsPagingItems.itemCount == 0

    Column(modifier = modifier) {
        // Month Navigation
        MonthNavigation(
            yearMonth = currentYearMonth,
            onPreviousClick = { onIntent(MyPageIntent.OnPreviousMonthClick) },
            onNextClick = { onIntent(MyPageIntent.OnNextMonthClick) },
        )

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

@Composable
private fun RecordTabContent(
    currentYearMonth: YearMonth,
    records: List<RecordItem>,
    onIntent: (MyPageIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // Month Navigation
        MonthNavigation(
            yearMonth = currentYearMonth,
            onPreviousClick = { onIntent(MyPageIntent.OnPreviousMonthClick) },
            onNextClick = { onIntent(MyPageIntent.OnNextMonthClick) },
        )

        if (records.isEmpty()) {
            EmptyState(
                title = "아직 러닝 기록이 없어요...",
                description = "지금 바로 러닝을 시작해봐요!",
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(36.dp),
            ) {
                items(records, key = { it.id }) { record ->
                    RecordCard(
                        record = record,
                        onClick = { onIntent(MyPageIntent.OnRecordClick(record.id)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthNavigation(
    yearMonth: YearMonth,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPreviousClick) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_left),
                contentDescription = "이전 달",
                tint = SixpackTheme.colors.gray900,
            )
        }

        Text(
            text = yearMonth.format(),
            style = SixpackTheme.typography.b1Medium,
            color = SixpackTheme.colors.gray900,
            modifier = Modifier.padding(horizontal = 12.dp),
        )

        IconButton(onClick = onNextClick) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right),
                contentDescription = "다음 달",
                tint = SixpackTheme.colors.gray900,
            )
        }
    }
}

@Composable
private fun PostGrid(
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

@Composable
private fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ill_empty_2),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(120.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = SixpackTheme.typography.t2Bold,
                color = SixpackTheme.colors.gray900,
                textAlign = TextAlign.Center,
            )
            Text(
                text = description,
                style = SixpackTheme.typography.b2Regular,
                color = SixpackTheme.colors.gray700,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun RecordCard(
    record: RecordItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = record.date,
            style = SixpackTheme.typography.b2Medium,
            color = SixpackTheme.colors.gray700,
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SixpackTheme.colors.gray0)
                    .clickable(onClick = onClick)
                    .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = record.time,
                            style = SixpackTheme.typography.c1Regular,
                            color = SixpackTheme.colors.gray700,
                        )
                        record.certificationStatus?.let { status ->
                            CertificationBadge(status = status)
                        }
                    }

                    Text(
                        text = "${record.distanceKm}km",
                        style = SixpackTheme.typography.h2Bold,
                        color = SixpackTheme.colors.gray900,
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        RecordStat(label = "시간", value = record.durationFormatted)
                        RecordStat(label = "페이스", value = record.paceFormatted)
                        RecordStat(label = "케이던스", value = "${record.cadence} spm")
                    }
                }

                if (record.certificationStatus == CertificationStatus.COMPLETED) {
                    Icon(
                        painter = painterResource(R.drawable.ill_character_success),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(72.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CertificationBadge(
    status: CertificationStatus,
    modifier: Modifier = Modifier,
) {
    val (text, backgroundColor, textColor) =
        when (status) {
            CertificationStatus.AVAILABLE -> Triple("인증 가능", SixpackTheme.colors.blue100, SixpackTheme.colors.blue600)
            CertificationStatus.COMPLETED -> Triple("인증 완료", SixpackTheme.colors.gray100, SixpackTheme.colors.gray700)
        }

    Box(
        modifier =
            modifier
                .background(backgroundColor, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = text,
            style = SixpackTheme.typography.c1Regular,
            color = textColor,
        )
    }
}

@Composable
private fun RecordStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = SixpackTheme.typography.c1Regular,
            color = SixpackTheme.colors.gray600,
        )
        Text(
            text = value,
            style = SixpackTheme.typography.c1Regular,
            color = SixpackTheme.colors.gray900,
        )
    }
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
                            friendCount = 7,
                            totalDistanceKm = 400.0,
                            certificationCount = 120,
                        ),
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
                            friendCount = 7,
                            totalDistanceKm = 0.0,
                            certificationCount = 0,
                        ),
                ),
            gridItemsPagingItems = gridItemsPagingItems,
            onIntent = {},
        )
    }
}
