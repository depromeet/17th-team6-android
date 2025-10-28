package com.dpm.sixpack.presentation.common.components.bottomsheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.dpm.sixpack.presentation.common.model.ReactingUserState
import com.dpm.sixpack.presentation.common.model.UserState
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
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    val emojiTabs = listOf(null) + reactionDetails.usersByEmoji.keys.toList()

    var selectedTabIndex by remember(reactionDetails) {
        val initialIndex =
            if (reactionDetails.selectedType.equals("ALL", ignoreCase = true)) {
                0
            } else {
                val emojiIndex =
                    emojiTabs.indexOfFirst {
                        it?.type.equals(
                            reactionDetails.selectedType,
                            ignoreCase = true,
                        )
                    }
                if (emojiIndex != -1) emojiIndex else 0
            }

        mutableIntStateOf(initialIndex)
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val minHeight = screenHeight * 5 / 8

    DoRunBottomSheetSlot(
        isBottomSheetVisible = isBottomSheetVisible,
        onDismissRequest = onDismissRequest,
        modifier = modifier.heightIn(min = minHeight),
        sheetState = sheetState,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
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

            LazyRow(
                modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(emojiTabs) { index, emoji ->
                    val count = reactionDetails.usersByEmoji[emoji]?.size ?: 0

                    if (emoji == null) {
                        AllTab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                        )
                    } else {
                        ReactionChip(
                            iconRes = emoji.iconRes,
                            count = count.toString(),
                            isReacted = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        },
        content = {
            val usersToShow =
                when (val selectedEmoji = emojiTabs[selectedTabIndex]) {
                    null -> reactionDetails.allUsersSortedByTime
                    else -> reactionDetails.usersByEmoji[selectedEmoji] ?: emptyList()
                }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(usersToShow) { user ->
                    ReactingUserRow(user = user, onUserProfileClick = onUserProfileClick)
                }
            }
        },
    )
}

@Composable
private fun AllTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier =
                modifier
                    .background(
                        color = if (selected) SixpackTheme.colors.blue600 else SixpackTheme.colors.gray50,
                        shape = RoundedCornerShape(16.dp),
                    ).padding(horizontal = 6.dp)
                    .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(id = R.string.feed_reaction_bottom_sheet_all_tab),
                style = SixpackTheme.typography.b2Medium,
                color = if (selected) SixpackTheme.colors.gray0 else SixpackTheme.colors.gray500,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .padding(vertical = 6.dp)
                        .widthIn(min = 40.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        VerticalDivider(
            modifier = Modifier.height(8.dp),
            thickness = 1.dp,
            color = SixpackTheme.colors.gray100,
        )
    }
}

@Composable
private fun ReactingUserRow(
    user: ReactingUserState,
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
            userImageUrl = user.user.profileImageUrl,
            userName = user.user.name,
            isMyReaction = user.user.isMe,
            onUserProfileClick = { onUserProfileClick(user.user.id) },
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            imageVector = ImageVector.vectorResource(id = user.emoji.iconRes),
            contentDescription = user.emoji.name,
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ReactionUsersBottomSheetPreview() {
    DoRunPreviewWrapper {
        val emojis = Emoji.entries
        val dummyNames = listOf("김육팩", "박복근", "이몸짱", "최헬스", "정운동", "강근육")
        val dummyTimeSuffixes = listOf("방금", "분 전", "시간 전", "일 전")

        val dummyUsers =
            (1..21).map { index ->
                val emoji = emojis[index % emojis.size]
                val name = dummyNames[index % dummyNames.size]
                val timeSuffix = dummyTimeSuffixes[index % dummyTimeSuffixes.size]
                val timeValue =
                    when (timeSuffix) {
                        "방금" -> ""
                        else -> (index * 2 + 1).toString()
                    }
                val reactedAt = if (timeValue.isEmpty()) timeSuffix else "$timeValue$timeSuffix"

                ReactingUserState(
                    user =
                        UserState(
                            id = index.toLong(),
                            name = name,
                            profileImageUrl = "",
                            isMe = index == 1, // 첫 번째 사용자만 '나'로 설정
                        ),
                    emoji = emoji,
                    reactedAt = reactedAt,
                )
            }

        val dummyReactionDetails =
            ReactionDetailsUiState(
                selectedType = "HEART",
                usersByEmoji = dummyUsers.groupBy { it.emoji },
                allUsersSortedByTime =
                    dummyUsers.sortedWith(
                        compareBy {
                            when {
                                it.reactedAt == "방금" -> 0
                                it.reactedAt.endsWith("분 전") ->
                                    it.reactedAt
                                        .removeSuffix("분 전")
                                        .trim()
                                        .toIntOrNull() ?: Int.MAX_VALUE

                                it.reactedAt.endsWith("시간 전") ->
                                    (
                                        it.reactedAt
                                            .removeSuffix("시간 전")
                                            .trim()
                                            .toIntOrNull()
                                            ?: Int.MAX_VALUE
                                    ) * 60

                                it.reactedAt.endsWith("일 전") ->
                                    (
                                        it.reactedAt
                                            .removeSuffix("일 전")
                                            .trim()
                                            .toIntOrNull()
                                            ?: Int.MAX_VALUE
                                    ) * 60 * 24

                                else -> Int.MAX_VALUE
                            }
                        },
                    ),
            )

        ReactionUsersBottomSheet(
            isBottomSheetVisible = true,
            onDismissRequest = {},
            reactionDetails = dummyReactionDetails,
            onUserProfileClick = {},
        )
    }
}

@Preview
@Composable
fun ReactingUserRowPreview() {
    DoRunPreviewWrapper {
        Column {
            ReactingUserRow(
                user =
                    ReactingUserState(
                        user =
                            UserState(
                                id = 1,
                                name = "김육팩",
                                profileImageUrl = "",
                                isMe = true,
                            ),
                        emoji = Emoji.HEART,
                        reactedAt = "1분 전",
                    ),
                onUserProfileClick = {},
            )
        }
    }
}
