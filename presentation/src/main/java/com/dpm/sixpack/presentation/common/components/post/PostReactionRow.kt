package com.dpm.sixpack.presentation.common.components.post

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.PostReaction
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 이모지 반응을 표시하는 행 컴포넌트
 */
@Composable
fun PostReactionRow(
    feedId: Long,
    reactions: List<PostReaction>,
    onReactionChipClick: (Emoji, Boolean) -> Unit,
    onReactionChipLongClick: (Emoji, List<PostReaction>) -> Unit,
    onAddReactionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            reactions.take(3).forEach { reaction ->
                ReactionChip(
                    count = reaction.count,
                    iconRes = reaction.emoji.iconRes,
                    isReacted = reaction.isReacted,
                    onClick = { onReactionChipClick(reaction.emoji, !reaction.isReacted) },
                    onLongClick = { onReactionChipLongClick(reaction.emoji, reactions) },
                )
            }
            if (reactions.size > 3) {
                val firstEmoji = reactions[0].emoji
                MoreReactionChip(
                    count = reactions.size - 3,
                    onClick = { onReactionChipLongClick(firstEmoji, reactions) },
                )
            }
            AddReactionButton(feedId, onAddReactionClick = onAddReactionClick)
        }
    }
}

/**
 * 이모지 추가 버튼
 */
@Composable
private fun AddReactionButton(
    feedId: Long,
    onAddReactionClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .clip(SixpackTheme.shapes.round16)
                .noRippleClickable(onClick = { onAddReactionClick() })
                .background(color = SixpackTheme.colors.gray50, shape = RoundedCornerShape(30.dp))
                .padding(vertical = 6.dp, horizontal = 10.dp),
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_add_reaction),
            contentDescription = stringResource(id = R.string.feed_post_reaction_row_add_reaction_description),
        )
    }
}

/**
 * 개별 이모지 반응 칩 컴포넌트
 */
@Composable
fun ReactionChip(
    count: String,
    @DrawableRes iconRes: Int,
    isReacted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit = {},
) {
    val backgroundColor = if (isReacted) SixpackTheme.colors.blue100 else SixpackTheme.colors.gray50
    val borderColor = if (isReacted) SixpackTheme.colors.blue600 else SixpackTheme.colors.gray0
    Row(
        modifier =
            modifier
                .clip(SixpackTheme.shapes.round16)
                .combinedClickable(
                    onClick = { onClick() },
                    onLongClick = { onLongClick() },
                )
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(16.dp),
                )
                .border(
                    1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(all = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Image(
            modifier =
                Modifier
                    .size(20.dp)
                    .clip(CircleShape),
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = stringResource(id = R.string.feed_post_reaction_row_emoji_description),
        )
        Spacer(modifier = Modifier.width(2.dp))

        Box(
            modifier = Modifier.width(18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = count,
                style = SixpackTheme.typography.b2Medium,
                color = SixpackTheme.colors.gray700,
            )
        }
    }
}

/**
 * 더 많은 리액션 칩
 */
@Composable
private fun MoreReactionChip(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .background(
                    color = SixpackTheme.colors.gray50,
                    shape = SixpackTheme.shapes.round16,
                )
                .padding(all = 6.dp)
                .clip(SixpackTheme.shapes.round16)
                .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "+$count",
            style = SixpackTheme.typography.b2Medium,
            color = SixpackTheme.colors.gray700,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .padding(vertical = 2.dp)
                    .widthIn(min = 40.dp),
        )
    }
}

@Preview
@Composable
fun PostReactionRowPreview1() {
    DoRunPreviewWrapper {
        PostReactionRow(
            reactions = listOf(PostReaction(Emoji.HEART, "10", true)),
            feedId = 1,
            onReactionChipClick = { _, _ -> },
            onAddReactionClick = {},
            onReactionChipLongClick = { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun PostReactionRowPreview3() {
    DoRunPreviewWrapper {
        PostReactionRow(
            reactions =
                listOf(
                    PostReaction(Emoji.HEART, "10", true),
                    PostReaction(Emoji.FIRE, "5", false),
                    PostReaction(Emoji.HEART, "2", false),
                ),
            feedId = 1,
            onReactionChipClick = { _, _ -> },
            onAddReactionClick = {},
            onReactionChipLongClick = { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun PostReactionRowPreview5() {
    DoRunPreviewWrapper {
        PostReactionRow(
            reactions =
                listOf(
                    PostReaction(Emoji.HEART, "10", true),
                    PostReaction(Emoji.FIRE, "5", false),
                    PostReaction(Emoji.HEART, "2", false),
                    PostReaction(Emoji.FIRE, "5", false),
                    PostReaction(Emoji.HEART, "2", false),
                ),
            feedId = 1,
            onReactionChipClick = { _, _ -> },
            onAddReactionClick = {},
            onReactionChipLongClick = { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun PostReactionRowPreviewEmpty() {
    DoRunPreviewWrapper {
        PostReactionRow(
            reactions = emptyList(),
            feedId = 1,
            onReactionChipClick = { _, _ -> },
            onAddReactionClick = {},
            onReactionChipLongClick = { _, _ -> },
        )
    }
}
