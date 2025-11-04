package com.dpm.sixpack.presentation.routes.feed.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.bottomsheet.EmojiSelectionBottomSheet
import com.dpm.sixpack.presentation.common.components.bottomsheet.ReactionUsersBottomSheet
import com.dpm.sixpack.presentation.common.components.post.PostDropDownActionType
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.PostReaction
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.common.model.PostingUserInfo
import com.dpm.sixpack.presentation.common.model.RunningSummary
import com.dpm.sixpack.presentation.common.model.UserInfo
import com.dpm.sixpack.presentation.routes.feed.component.FeedFTAButton
import com.dpm.sixpack.presentation.routes.feed.component.FeedTopBar
import com.dpm.sixpack.presentation.routes.feed.component.dialog.PostDeleteDialog
import com.dpm.sixpack.presentation.routes.feed.component.dialog.PostReportDialog
import com.dpm.sixpack.presentation.routes.feed.contract.FeedUiState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedCalenderUiState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedDateUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    state: FeedUiState,
    feedPagingItems: LazyPagingItems<PostResource>,
    modifier: Modifier = Modifier,
    onTopBarGroupIconClick: () -> Unit = {},
    onTopBarAlarmIconClick: () -> Unit = {},
    // Calendar
    onDateSelected: (LocalDate) -> Unit = {},
    onVisibleWeeksChanged: (LocalDate) -> Unit = {},
    // Certified Users
    onCertifiedUsersClick: () -> Unit = {},
    // Post
    onPostUserProfileClick: (Long, Boolean) -> Unit = { _, _ -> },
    onPostMenuClick: (Long) -> Unit = {},
    onPostImageClick: (PostResource) -> Unit = {},
    onPostReactionClick: (PostResource, Emoji, Boolean) -> Unit = { _, _, _ -> },
    onPostReactionLongClick: (Long, List<PostReaction>, Emoji) -> Unit = { _, _, _ -> },
    onPostAddReactionClick: (PostResource) -> Unit = {},
    onDropDownMenuClick: (PostResource, PostDropDownActionType) -> Unit = { _, _ -> },
    // BottomSheet
    onBottomSheetDismiss: () -> Unit = {},
    onUserReactionSheetUserProfileClick: (Long, Boolean) -> Unit = { _, _ -> },
    onUserReactionSheetTabClick: (Emoji) -> Unit = {},
    onEmojiSelected: (Emoji) -> Unit = {},
    // Dialog
    onDialogDismiss: () -> Unit = {},
    onDialogConfirmClick: () -> Unit = {},
    // FAB
    onFTAButtonClick: () -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    val isScrolled by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0 || lazyListState.firstVisibleItemScrollOffset > 0
        }
    }

    val isInitialLoad by remember {
        derivedStateOf {
            feedPagingItems.itemCount == 0 &&
                feedPagingItems.loadState.refresh is LoadState.Loading &&
                state.feedDateState == FeedDateUiState.PostsAvailable
        }
    }

    LaunchedEffect(feedPagingItems.loadState.refresh) {
        if (isRefreshing && feedPagingItems.loadState.refresh !is LoadState.Loading) {
            delay(300)
            isRefreshing = false
        }
    }

    // Timeout: 5초 이상 로딩 시 자동 종료 (UX: 너무 긴 로딩 방지)
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(5000)
            isRefreshing = false
        }
    }

    Scaffold(
        modifier =
            modifier
                .fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                FeedTopBar(
                    onGroupIconClick = onTopBarGroupIconClick,
                    onAlarmIconClick = onTopBarAlarmIconClick,
                )

                if (isScrolled) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = SixpackTheme.colors.gray100,
                    )
                }
            }
        },
        containerColor = SixpackTheme.colors.gray0,
        contentColor = SixpackTheme.colors.gray900,
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        val contentModifier =
            Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    if (state.selectedPostMenuId != null) {
                        onPostMenuClick(-1)
                    }
                }

        if (state.feedDateState == FeedDateUiState.PostsAvailable) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    feedPagingItems.refresh()
                },
                modifier = contentModifier,
                state = pullToRefreshState,
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = pullToRefreshState,
                        isRefreshing = isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter),
                        color = SixpackTheme.colors.blue600,
                        containerColor = Color.White,
                    )
                },
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier =
                            Modifier
                                .padding(paddingValues)
                                .fillMaxWidth(),
                        state = lazyListState,
                    ) {
                        // Calendar
                        feedCalendarItem(
                            calendarState = state.calendarState,
                            onDateSelected = onDateSelected,
                            onWeekDisplayed = onVisibleWeeksChanged,
                        )

                        // Divider
                        feedDividerItem()

                        when (state.feedDateState) {
                            FeedDateUiState.NoPostsAndCertifiable -> {
                                item(key = "empty_certifiable") { EmptyStateCertifiable() }
                            }

                            FeedDateUiState.NoPostsAndExpired -> {
                                item(key = "empty_expired") { EmptyStateExpired() }
                            }

                            FeedDateUiState.PostsAvailable -> {
                                feedContentItems(
                                    isInitialLoad = isInitialLoad,
                                    postingUserInfo = state.postingUserInfo,
                                    feedPagingItems = feedPagingItems,
                                    selectedPostMenuId = state.selectedPostMenuId,
                                    onCertifiedUsersClick = onCertifiedUsersClick,
                                    onPostMenuClick = onPostMenuClick,
                                    onDropDownMenuClick = onDropDownMenuClick,
                                    onPostImageClick = onPostImageClick,
                                    onPostUserProfileClick = onPostUserProfileClick,
                                    onReactionClick = onPostReactionClick,
                                    onReactionLongClick = onPostReactionLongClick,
                                    onAddReactionClick = onPostAddReactionClick,
                                )
                            }
                        }
                    }

                    FeedFTAButton(
                        enabled = state.feedDateState != FeedDateUiState.NoPostsAndExpired,
                        onFTAButtonClick = onFTAButtonClick,
                        modifier = Modifier.align(Alignment.BottomEnd),
                    )
                }
            }
        } else {
            // PostsAvailable이 아닐 때는 PullToRefresh 비활성화
            Box(modifier = contentModifier) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier =
                            Modifier
                                .padding(paddingValues)
                                .fillMaxWidth(),
                        state = lazyListState,
                    ) {
                        // Calendar
                        feedCalendarItem(
                            calendarState = state.calendarState,
                            onDateSelected = onDateSelected,
                            onWeekDisplayed = onVisibleWeeksChanged,
                        )

                        // Divider
                        feedDividerItem()

                        when (state.feedDateState) {
                            FeedDateUiState.NoPostsAndCertifiable -> {
                                item(key = "empty_certifiable") { EmptyStateCertifiable() }
                            }

                            FeedDateUiState.NoPostsAndExpired -> {
                                item(key = "empty_expired") { EmptyStateExpired() }
                            }

                            FeedDateUiState.PostsAvailable -> {
                                // 이 분기는 도달하지 않음 (외부 if 문에서 처리됨)
                            }
                        }
                    }

                    FeedFTAButton(
                        enabled = state.feedDateState != FeedDateUiState.NoPostsAndExpired,
                        onFTAButtonClick = onFTAButtonClick,
                        modifier = Modifier.align(Alignment.BottomEnd),
                    )
                }
            }
        }
    }
    ReactionUsersBottomSheet(
        isBottomSheetVisible = state.bottomSheetState.reactionUsers,
        onDismissRequest = onBottomSheetDismiss,
        reactionDetails = state.reactionDetailsUiState,
        onUserProfileClick = onUserReactionSheetUserProfileClick,
        onTabSelected = onUserReactionSheetTabClick,
    )

    EmojiSelectionBottomSheet(
        isBottomSheetVisible = state.bottomSheetState.emojiSelection,
        onDismissRequest = onBottomSheetDismiss,
        onEmojiSelected = onEmojiSelected,
    )

    if (state.dialogState.deleteFeedId != null) {
        PostDeleteDialog(
            onDismissRequest = onDialogDismiss,
            onCancelClick = onDialogDismiss,
            onConfirmClick = onDialogConfirmClick,
        )
    }

    if (state.dialogState.reportFeedId != null) {
        PostReportDialog(
            onDismissRequest = onDialogDismiss,
            onCancelClick = onDialogDismiss,
            onConfirmClick = onDialogConfirmClick,
        )
    }
}

@Composable
private fun EmptyStateExpired(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.img_feed_empty_expired),
            contentDescription = stringResource(id = R.string.feed_empty_state_no_certification_description),
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.feed_empty_state_no_certification_title),
            style = SixpackTheme.typography.t2Bold,
            color = SixpackTheme.colors.gray900,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.feed_empty_state_no_certification_subtitle),
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray700,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun EmptyStateCertifiable(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.img_feed_empty_certifiable),
            contentDescription = stringResource(id = R.string.feed_empty_state_waiting_certification_description),
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.feed_empty_state_waiting_certification_title),
            style = SixpackTheme.typography.t2Bold,
            color = SixpackTheme.colors.gray900,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.feed_empty_state_waiting_certification_subtitle),
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray700,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FeedScreenPreview() {
    val dummyPosts =
        listOf(
            PostResource(
                feedId = 1L,
                postImageUrl = "",
                user =
                    PostingUserInfo(
                        user = UserInfo(id = 1L, name = "김육팩", profileImageUrl = "", isMe = true),
                        postingTime = "10분 전",
                    ),
                runningInfo = RunningSummary(totalDistance = "5.2", totalTime = "1800"),
                reactions = listOf(PostReaction(Emoji.HEART, "10", true)),
            ),
            PostResource(
                feedId = 2L,
                postImageUrl = "",
                user =
                    PostingUserInfo(
                        user = UserInfo(id = 2L, name = "박복근", profileImageUrl = "", isMe = false),
                        postingTime = "30분 전",
                    ),
                runningInfo = RunningSummary(totalDistance = "3.1", totalTime = "1200"),
            ),
        )

    val dummyPagingDataFlow = flowOf(PagingData.from(dummyPosts))

    val dummyPagingItems = dummyPagingDataFlow.collectAsLazyPagingItems()

    val dummyState =
        FeedUiState(
            calendarState =
                FeedCalenderUiState(
                    postCounts = mapOf(LocalDate.now() to 2, LocalDate.now().minusDays(1) to 5),
                ),
            postingUserInfo =
                listOf(
                    PostingUserInfo(
                        user = UserInfo(id = 1L, name = "김육팩", profileImageUrl = "", isMe = true),
                        postingTime = "",
                    ),
                    PostingUserInfo(
                        user = UserInfo(id = 2L, name = "박복근", profileImageUrl = "", isMe = false),
                        postingTime = "",
                    ),
                ),
            feedDateState = FeedDateUiState.PostsAvailable,
        )

    DoRunPreviewWrapper {
        FeedScreen(
            state = dummyState,
            feedPagingItems = dummyPagingItems,
        )
    }
}

@Preview
@Composable
private fun FeedScreenEmptyExpiredPreview() {
    val dummyPagingDataFlow = flowOf(PagingData.empty<PostResource>())

    val dummyPagingItems = dummyPagingDataFlow.collectAsLazyPagingItems()
    DoRunPreviewWrapper {
        FeedScreen(
            state = FeedUiState(feedDateState = FeedDateUiState.NoPostsAndExpired),
            feedPagingItems = dummyPagingItems,
        )
    }
}

@Preview
@Composable
private fun FeedScreenEmptyCertifiablePreview() {
    val dummyPagingDataFlow = flowOf(PagingData.empty<PostResource>())

    val dummyPagingItems = dummyPagingDataFlow.collectAsLazyPagingItems()
    DoRunPreviewWrapper {
        FeedScreen(
            state = FeedUiState(feedDateState = FeedDateUiState.NoPostsAndCertifiable),
            feedPagingItems = dummyPagingItems,
        )
    }
}
