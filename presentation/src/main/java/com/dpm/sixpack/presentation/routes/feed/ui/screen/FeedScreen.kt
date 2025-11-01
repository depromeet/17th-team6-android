package com.dpm.sixpack.presentation.routes.feed.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.dpm.sixpack.presentation.common.components.post.FeedPostCard
import com.dpm.sixpack.presentation.common.components.post.PostDropDownActionType
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.PostReaction
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.common.model.PostingUserInfo
import com.dpm.sixpack.presentation.common.model.RunningSummary
import com.dpm.sixpack.presentation.common.model.UserInfo
import com.dpm.sixpack.presentation.routes.feed.component.CertificationCountView
import com.dpm.sixpack.presentation.routes.feed.component.FeedTopBar
import com.dpm.sixpack.presentation.routes.feed.component.calender.FeedWeeklyCalendar
import com.dpm.sixpack.presentation.routes.feed.component.dialog.PostDeleteDialog
import com.dpm.sixpack.presentation.routes.feed.component.dialog.PostReportDialog
import com.dpm.sixpack.presentation.routes.feed.contract.FeedUiState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedCalenderUiState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedDateUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

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
    onDropDownMenuClick: (Long, PostDropDownActionType) -> Unit = { _, _ -> },

    // BottomSheet
    onBottomSheetDismiss: () -> Unit = {},
    onUserReactionSheetUserProfileClick: (Long, Boolean) -> Unit = { _, _ -> },
    onUserReactionSheetTabClick: (Emoji) -> Unit = {},
    onEmojiSelected: (Emoji) -> Unit = {},

    // Dialog
    onDialogDismiss: () -> Unit = {},
    onDialogConfirmClick: () -> Unit = {},

    // FAB
    onFTAButtonClick: () -> Unit = {}
) {
    val lazyListState = rememberLazyListState()

    val isScrolled by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0 || lazyListState.firstVisibleItemScrollOffset > 0
        }
    }

    Scaffold(
        modifier = modifier
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
                        color = SixpackTheme.colors.gray100
                    )
                }
            }
        },
        containerColor = SixpackTheme.colors.gray0,
        contentColor = SixpackTheme.colors.gray900,
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth(),
                state = lazyListState
            ) {
                item {
                    FeedWeeklyCalendar(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 16.dp),
                        feedCalenderUiState = state.calendarState,
                        onDateSelected = onDateSelected,
                        onWeekDisplayed = onVisibleWeeksChanged
                    )
                }

                item {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        thickness = 1.dp,
                        color = SixpackTheme.colors.gray50
                    )
                }

                when (state.feedDateState) {
                    FeedDateUiState.NoPostsAndCertifiable -> {
                        item { EmptyStateCertifiable() }
                    }

                    FeedDateUiState.NoPostsAndExpired -> {
                        item { EmptyStateExpired() }
                    }

                    FeedDateUiState.PostsAvailable -> {
                        item {
                            CertificationCountView(
                                users = state.postingUserInfo,
                                onViewClick = onCertifiedUsersClick,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }

                        item { Spacer(modifier = Modifier.height(32.dp)) }


                        items(
                            count = feedPagingItems.itemCount,
                            key = { feedPagingItems.peek(it)?.feedId!!.toInt() }
                        ) { index ->
                            feedPagingItems[index]?.let { post ->
                                val isMenuExpanded = (state.selectedPostMenuId != null)
                                FeedPostCard(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    postDetail = post,
                                    isMenuExpanded = isMenuExpanded,
                                    onMenuClick = { onPostMenuClick(post.feedId) },
                                    onDropDownMenuClick = { action -> onDropDownMenuClick(post.feedId, action) },
                                    onPostImageClick = { onPostImageClick(post) },
                                    onPostUserProfileClick = { userId, isMe -> onPostUserProfileClick(userId, isMe) },
                                    onReactionChipClick = { emoji, isReacted ->
                                        onPostReactionClick(
                                            post,
                                            emoji,
                                            isReacted
                                        )
                                    },
                                    onReactionChipLongClick = { emoji, reactions ->
                                        onPostReactionLongClick(
                                            post.feedId,
                                            reactions,
                                            emoji
                                        )
                                    },
                                    onAddReactionClick = { onPostAddReactionClick(post) }
                                )
                            }
                            Spacer(Modifier.height(40.dp))
                        }

                        feedPagingItems.loadState.apply {
                            when {
                                refresh is LoadState.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier.fillParentMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) { CircularProgressIndicator() }
                                    }
                                }

                                append is LoadState.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 2.dp
                                            )
                                        }
                                    }
                                }

                                refresh is LoadState.Error -> {
                                    item {
                                        Column(
                                            modifier = Modifier.fillParentMaxSize(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = stringResource(
                                                    id = R.string.feed_load_state_error_message,
                                                    (refresh as LoadState.Error).error.message ?: ""
                                                )
                                            )
                                            Button(onClick = { feedPagingItems.retry() }) {
                                                Text(stringResource(id = R.string.feed_load_state_retry_button))
                                            }
                                        }
                                    }
                                }

                                append is LoadState.Error -> {
                                    item {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = stringResource(
                                                    id = R.string.feed_load_state_error_message,
                                                    (append as LoadState.Error).error.message ?: ""
                                                )
                                            )
                                            Button(onClick = { feedPagingItems.retry() }) {
                                                Text(stringResource(id = R.string.feed_load_state_retry_button))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        bottom = 16.dp,
                        end = 20.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SixpackTheme.colors.blue600)
                        .clickable(enabled = state.feedDateState != FeedDateUiState.NoPostsAndExpired) {  onFTAButtonClick() }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_plus),
                        contentDescription = stringResource(id = R.string.feed_floating_action_button_description),
                        modifier = Modifier.size(24.dp),
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
        onTabSelected = onUserReactionSheetTabClick
    )

    EmojiSelectionBottomSheet(
        isBottomSheetVisible = state.bottomSheetState.emojiSelection,
        onDismissRequest = onBottomSheetDismiss,
        onEmojiSelected = onEmojiSelected
    )

    if (state.dialogState.deleteFeedId != null) {
        PostDeleteDialog(
            onDismissRequest = onDialogDismiss,
            onCancelClick = onDialogDismiss,
            onConfirmClick = onDialogConfirmClick
        )
    }

    if (state.dialogState.reportFeedId != null) {
        PostReportDialog(
            onDismissRequest = onDialogDismiss,
            onCancelClick = onDialogDismiss,
            onConfirmClick = onDialogConfirmClick
        )
    }
}

@Composable
fun EmptyStateExpired(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            imageVector = Icons.Filled.Image,
            contentDescription = stringResource(id = R.string.feed_empty_state_no_certification_description),
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.feed_empty_state_no_certification_title),
            style = SixpackTheme.typography.t2Bold,
            color = SixpackTheme.colors.gray900,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.feed_empty_state_no_certification_subtitle),
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray700,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmptyStateCertifiable(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            imageVector = Icons.Filled.Image, // TODO: 여기에 실제 그래픽 리소스(Painter) 삽입
            contentDescription = stringResource(id = R.string.feed_empty_state_waiting_certification_description),
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.feed_empty_state_waiting_certification_title),
            style = SixpackTheme.typography.t2Bold,
            color = SixpackTheme.colors.gray900,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.feed_empty_state_waiting_certification_subtitle),
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray700,
            textAlign = TextAlign.Center
        )
    }
}


@Preview(showBackground = true)
@Composable
fun FeedScreenPreview() {
    val dummyPosts = listOf(
        PostResource(
            feedId = 1L,
            postImageUrl = "",
            user = PostingUserInfo(
                user = UserInfo(id = 1L, name = "김육팩", profileImageUrl = "", isMe = true),
                postingTime = "10분 전"
            ),
            runningInfo = RunningSummary(totalDistance = "5.2", totalTime = "1800"),
            reactions = listOf(PostReaction(Emoji.HEART, "10", true))
        ),
        PostResource(
            feedId = 2L,
            postImageUrl = "",
            user = PostingUserInfo(
                user = UserInfo(id = 2L, name = "박복근", profileImageUrl = "", isMe = false),
                postingTime = "30분 전"
            ),
            runningInfo = RunningSummary(totalDistance = "3.1", totalTime = "1200")
        )
    )


    val dummyPagingDataFlow = flowOf(PagingData.from(dummyPosts))

    val dummyPagingItems = dummyPagingDataFlow.collectAsLazyPagingItems()

    val dummyState = FeedUiState(
        calendarState = FeedCalenderUiState(
            postCounts = mapOf(LocalDate.now() to 2, LocalDate.now().minusDays(1) to 5)
        ),
        postingUserInfo = listOf(
            PostingUserInfo(
                user = UserInfo(id = 1L, name = "김육팩", profileImageUrl = "", isMe = true),
                postingTime = ""
            ),
            PostingUserInfo(
                user = UserInfo(id = 2L, name = "박복근", profileImageUrl = "", isMe = false),
                postingTime = ""
            )
        ),
        feedDateState = FeedDateUiState.PostsAvailable
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
