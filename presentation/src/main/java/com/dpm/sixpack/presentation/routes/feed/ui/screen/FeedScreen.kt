package com.dpm.sixpack.presentation.routes.feed.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.dpm.sixpack.domain.model.Feed
import com.dpm.sixpack.presentation.common.components.bottomsheet.EmojiSelectionBottomSheet
import com.dpm.sixpack.presentation.common.components.bottomsheet.ReactionUsersBottomSheet
import com.dpm.sixpack.presentation.common.components.post.FeedPostCard
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.routes.feed.component.CertificationCountView
import com.dpm.sixpack.presentation.routes.feed.component.FeedTopBar
import com.dpm.sixpack.presentation.routes.feed.component.calender.FeedWeeklyCalendar
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedBottomSheetState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedUiState
import java.time.LocalDate

@Composable
fun FeedScreen(
    state: FeedUiState,
    feedPagingItems: LazyPagingItems<Feed>,
    onTopBarGroupIconClick: () -> Unit,
    onTopBarAlarmIconClick: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onVisibleWeeksChanged: (LocalDate) -> Unit,
    onCertifiedUsersClick: () -> Unit,
    onPostUserProfileClick: (Int, Boolean) -> Unit,
    onPostMenuClick: (Int) -> Unit,
    onPostMapImageClick: (Int) -> Unit,
    onPostReactionClick: (Int, Emoji) -> Unit,
    onPostReactionLongClick: (Int, String) -> Unit,
    onPostAddReactionClick: (Int) -> Unit,
    onBottomSheetDismiss: () -> Unit,
    onBottomSheetUserProfileClick: (Int) -> Unit,
    onEmojiSelected: (Emoji) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FeedTopBar(
            onGroupIconClick = onTopBarGroupIconClick,
            onAlarmIconClick = onTopBarAlarmIconClick,
        )

        FeedWeeklyCalendar(
            feedCalenderUiState = state.calendarState,
            onDateSelected = onDateSelected,
            onVisibleWeeksChanged = onVisibleWeeksChanged
        )

        Spacer(modifier = Modifier.height(24.dp))

        CertificationCountView(
            modifier = Modifier.clickable(onClick = onCertifiedUsersClick),
            users = state.certifiedUsers,
            isMeCertified = state.isMeCertified,
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(feedPagingItems.itemCount) { index ->
                feedPagingItems[index]?.let { feed ->
                    FeedPostCard(
                        postDetail = feed, // This needs a mapper
                        currentUserName = state.currentUserName,
                        onMenuClick = { onPostMenuClick(feed.feedId) },
                        onReactionChipClick = { emojiType ->
                            val emoji = Emoji.from(emojiType)
                            if (emoji != Emoji.UNKNOWN) {
                                onPostReactionClick(feed.feedId, emoji)
                            }
                        },
                        onReactionChipLongClick = { emojiType -> onPostReactionLongClick(feed.feedId, emojiType) },
                        onAddReactionClick = { onPostAddReactionClick(feed.feedId) }
                    )
                }
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
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                    }

                    refresh is LoadState.Error -> {
                        item { Text(text = "Error: ${(refresh as LoadState.Error).error.message}") }
                    }

                    append is LoadState.Error -> {
                        item { Text(text = "Error: ${(append as LoadState.Error).error.message}") }
                    }
                }
            }
        }
    }

    when (val bottomSheet = state.bottomSheetState) {
        is FeedBottomSheetState.ReactionUsers -> {
            ReactionUsersBottomSheet(
                isBottomSheetVisible = true,
                onDismissRequest = onBottomSheetDismiss,
                reactionDetails = bottomSheet.details,
                onUserProfileClick = { userId -> onBottomSheetUserProfileClick(userId) }
            )
        }

        is FeedBottomSheetState.EmojiSelection -> {
            EmojiSelectionBottomSheet(
                isBottomSheetVisible = true,
                onDismissRequest = onBottomSheetDismiss,
                onEmojiSelected = onEmojiSelected
            )
        }

        FeedBottomSheetState.Hidden -> {
            // Do nothing
        }
    }
}

