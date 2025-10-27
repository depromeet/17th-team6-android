package com.dpm.sixpack.presentation.common.components.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.PostReactionUiState
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 이모지 반응을 표시하는 행 컴포넌트
 */
@Composable
fun PostReactionRow(
    reactions: List<PostReactionUiState>,
    onReactionChipClick: (String) -> Unit,
    onReactionChipLongClick: (String) -> Unit,
    onAddReactionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            reactions.take(3).forEach { reaction ->
                ReactionChip(
                    reaction = reaction,
                    onClick = { onReactionChipClick(reaction.emoji.type) },
                    onLongClick = { onReactionChipLongClick(reaction.emoji.type) },
                )
            }
            if (reactions.size > 3) {
                MoreReactionChip(
                    count = reactions.size - 3,
                    onClick = { onReactionChipLongClick("ALL") },
                )

                AddReactionButton(onAddReactionClick = onAddReactionClick)
            }
        }
    }
}

/**
 * 이모지 추가 버튼
 */
@Composable
private fun AddReactionButton(onAddReactionClick: () -> Unit) {
    Box(
        modifier = Modifier
            .noRippleClickable(onClick = onAddReactionClick)
            .background(color = SixpackTheme.colors.gray50, shape = RoundedCornerShape(30.dp))
            .padding(vertical = 6.dp, horizontal = 10.dp)
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_add_reaction),
            contentDescription = "이모지 추가",
        )
    }
}

/**
 * 개별 이모지 반응 칩 컴포넌트
 */
@Composable
private fun ReactionChip(
    reaction: PostReactionUiState,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (reaction.isReacted) SixpackTheme.colors.blue100 else SixpackTheme.colors.gray50
    val borderColor = if (reaction.isReacted) SixpackTheme.colors.blue600 else SixpackTheme.colors.gray0
    Row(
        modifier = modifier
            .combinedClickable(
                onClick = { onClick(reaction.emoji.type) },
                onLongClick = { onLongClick(reaction.emoji.type) }
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                1.dp, color = borderColor, shape = RoundedCornerShape(16.dp)
            )
            .padding(all = 6.dp), // 패딩을 background와 border 안쪽으로 이동
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape),
            imageVector = ImageVector.vectorResource(reaction.emoji.iconRes),
            contentDescription = "이모지",
        )
        Spacer(modifier = Modifier.width(2.dp))

        Box(
            modifier = Modifier.width(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = reaction.count.toString(),
                style = SixpackTheme.typography.b2Medium,
                color = SixpackTheme.colors.gray700
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = SixpackTheme.colors.gray50,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(all = 6.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "+$count",
            style = SixpackTheme.typography.b2Medium,
            color = SixpackTheme.colors.gray700,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 2.dp)
                .widthIn(min = 40.dp)
        )
    }
}

@Preview
@Composable
fun PostReactionRowPreview1() {
    DoRunPreviewWrapper {
        PostReactionRow(
            reactions = listOf(
                PostReactionUiState(Emoji.HEART, 10, true)
            ),
            onReactionChipClick = {},
            onAddReactionClick = {},
            onReactionChipLongClick = {}
        )
    }
}

@Preview
@Composable
private fun PostReactionRowPreview3() {
    DoRunPreviewWrapper {
        PostReactionRow(
            reactions = listOf(
                PostReactionUiState(Emoji.HEART, 10, true),
                PostReactionUiState(Emoji.FIRE, 5, false),
                PostReactionUiState(Emoji.SHOOT, 2, false)
            ),
            onReactionChipClick = {},
            onAddReactionClick = {},
            onReactionChipLongClick = {}
        )
    }
}

@Preview
@Composable
private fun PostReactionRowPreview5() {
    DoRunPreviewWrapper {
        PostReactionRow(
            reactions = listOf(
                PostReactionUiState(Emoji.HEART, 10, true),
                PostReactionUiState(Emoji.FIRE, 5, false),
                PostReactionUiState(Emoji.SHOOT, 2, false),
                PostReactionUiState(Emoji.FIRE, 5, false),
                PostReactionUiState(Emoji.SHOOT, 2, false)
            ),
            onReactionChipClick = {},
            onAddReactionClick = {},
            onReactionChipLongClick = {}
        )
    }
}

@Preview
@Composable
private fun PostReactionRowPreviewEmpty() {
    DoRunPreviewWrapper {
        PostReactionRow(
            reactions = emptyList(),
            onReactionChipClick = {},
            onAddReactionClick = {},
            onReactionChipLongClick = {}
        )
    }
}
