package com.dpm.sixpack.presentation.common.components.bottomsheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.post.ReactionChip
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.PostReaction
import com.dpm.sixpack.presentation.common.model.ReactingUserInfo
import com.dpm.sixpack.presentation.common.model.UserInfo
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.ReactionDetailsUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReactionUsersBottomSheet(
    isBottomSheetVisible: Boolean,
    onDismissRequest: () -> Unit,
    reactionDetails: ReactionDetailsUiState,
    onUserProfileClick: (Long) -> Unit,
    onTabSelected: (Emoji) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val minHeight = screenHeight * 5 / 8

    DoRunBottomSheetSlot(
        isBottomSheetVisible = isBottomSheetVisible,
        onDismissRequest = onDismissRequest,
        modifier = modifier.heightIn(min = minHeight),
        sheetState = sheetState,
        title = {
            when (reactionDetails) {
                is ReactionDetailsUiState.Loading -> {
                    ReactionHeaderShimmer()
                }

                is ReactionDetailsUiState.Success -> {
                    ReactionHeaderSuccess(
                        reactionDetails = reactionDetails,
                        onTabClick = onTabSelected
                    )
                }
            }
        },
        content = {
            when (reactionDetails) {
                is ReactionDetailsUiState.Loading -> {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(5) {
                            ReactingUserRowShimmer()
                        }
                    }
                }

                is ReactionDetailsUiState.Success -> {
                    val selectedEmoji = reactionDetails.selectedEmoji

                    val usersToShow =
                        reactionDetails.reactions.find { it.emoji == selectedEmoji }?.users ?: emptyList()

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(usersToShow, key = { it.user.id }) { user ->
                            ReactingUserRow(
                                reactingUser = user,
                                onUserProfileClick = onUserProfileClick
                            )
                        }
                    }
                }
            }
        },
    )
}


@Composable
private fun ReactionHeaderSuccess(
    reactionDetails: ReactionDetailsUiState.Success,
    onTabClick: (Emoji) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.feed_reaction_bottom_sheet_title),
            style = SixpackTheme.typography.t1Bold,
            color = SixpackTheme.colors.gray900,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = " ${reactionDetails.allUsersSortedByTime.size}",
            style = SixpackTheme.typography.t1Bold,
            color = SixpackTheme.colors.gray500,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    val emojiTabs = reactionDetails.reactions.map { it.emoji }
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(emojiTabs, key = { it.type }) { emoji ->
            val selected = reactionDetails.selectedEmoji == emoji
            val reaction = reactionDetails.reactions.find { it.emoji == emoji }

            ReactionChip(
                iconRes = emoji.iconRes,
                count = reaction?.count ?: "0",
                isReacted = selected,
                onClick = { onTabClick(emoji) },
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun ReactingUserRow(
    reactingUser: ReactingUserInfo,
    onUserProfileClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ReactionUserInfo(
            userImageUrl = reactingUser.user.profileImageUrl,
            userName = reactingUser.user.name,
            isMyReaction = reactingUser.user.isMe,
            onUserProfileClick = { onUserProfileClick(reactingUser.user.id) },
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            imageVector = ImageVector.vectorResource(id = reactingUser.emoji.iconRes),
            contentDescription = reactingUser.emoji.name,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun ReactionUserInfo(
    userImageUrl: String,
    userName: String,
    isMyReaction: Boolean,
    onUserProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(52.dp)
                    .border(width = 1.dp, color = SixpackTheme.colors.gray200, shape = CircleShape)
                    .noRippleClickable(onClick = onUserProfileClick),
        ) {
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(userImageUrl)
                        .crossfade(true)
                        .build(),
                contentDescription =
                    stringResource(
                        id = R.string.feed_reaction_bottom_sheet_user_profile_image_description,
                    ),
                modifier =
                    Modifier
                        .size(52.dp)
                        .clip(CircleShape),
                placeholder = ColorPainter(SixpackTheme.colors.gray500),
                error = ColorPainter(SixpackTheme.colors.gray100),
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = userName,
            style = SixpackTheme.typography.t2Bold,
            color = SixpackTheme.colors.gray900,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.width(4.dp))

        if (isMyReaction) {
            Surface(
                shape = CircleShape,
                color = SixpackTheme.colors.blue600,
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    text = stringResource(id = R.string.feed_reaction_bottom_sheet_my_reaction_badge),
                    color = SixpackTheme.colors.gray0,
                    style = SixpackTheme.typography.c1Medium,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}


@Composable
private fun ReactionHeaderShimmer() {
    Column {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(24.dp)
                .background(SixpackTheme.colors.gray100, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(4) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(32.dp)
                        .background(SixpackTheme.colors.gray100, RoundedCornerShape(16.dp))
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun ReactingUserRowShimmer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // 프로필 이미지 스켈레톤
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(SixpackTheme.colors.gray100, CircleShape)
        )

        Box(
            modifier = Modifier
                .width(120.dp)
                .height(20.dp)
                .background(SixpackTheme.colors.gray100, RoundedCornerShape(4.dp))
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Success State")
@Composable
fun ReactionUsersBottomSheetPreview() {
    DoRunPreviewWrapper {
        var selectedEmoji by remember { mutableStateOf(Emoji.HEART) }
        val dummyUsersHeart = (1..10).map {
            ReactingUserInfo(
                user = UserInfo(id = it.toLong(), name = "하트유저 $it", profileImageUrl = "", isMe = false),
                reactedAt = "${it}분전",
                emoji = Emoji.CONGRATS
            )
        }
        val dummyUsersFire = (11..15).map {
            ReactingUserInfo(
                user = UserInfo(id = it.toLong(), name = "불꽃유저 ${it - 10}", profileImageUrl = "", isMe = false),
                reactedAt = "$it 분 전",
                emoji = Emoji.FIRE
            )
        }
        val dummyReactions = listOf(
            PostReaction(Emoji.HEART, "10", true, dummyUsersHeart),
            PostReaction(Emoji.FIRE, "5", false, dummyUsersFire)
        )

        val allUsersSorted = dummyReactions.flatMap { it.users }
        val dummyReactionDetails = ReactionDetailsUiState.Success(
            reactions = dummyReactions,
            allUsersSortedByTime = allUsersSorted,
            selectedEmoji = selectedEmoji
        )

        ReactionUsersBottomSheet(
            isBottomSheetVisible = true,
            onDismissRequest = {},
            reactionDetails = dummyReactionDetails,
            onUserProfileClick = {},
            onTabSelected = { newEmoji ->
                selectedEmoji = newEmoji
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Loading State (Shimmer)")
@Composable
fun ReactionUsersBottomSheetLoadingPreview() {
    DoRunPreviewWrapper {
        ReactionUsersBottomSheet(
            isBottomSheetVisible = true,
            onDismissRequest = {},
            reactionDetails = ReactionDetailsUiState.Loading,
            onUserProfileClick = {},
            onTabSelected = {}
        )
    }
}
