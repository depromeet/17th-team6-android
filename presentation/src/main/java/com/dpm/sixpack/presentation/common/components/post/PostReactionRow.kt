package com.dpm.sixpack.presentation.common.components.post

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.model.PostReactionUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 이모지 반응을 표시하는 행 컴포넌트
 */
@Composable
fun PostReactionRow(
    reactions: List<PostReactionUiState>,
    onReactionClick: (String) -> Unit,
    onAddReactionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 기존 이모지 반응들
        reactions.forEach { reaction ->
            ReactionChip(
                emoji = reaction.emojiType,
                count = reaction.count,
                onClick = { onReactionClick(reaction.emojiType) }
            )
        }

        // 이모지 추가 버튼
        IconButton(
            onClick = onAddReactionClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_input_add),
                contentDescription = "이모지 추가",
                tint = SixpackTheme.colors.gray500
            )
        }
    }
}

/**
 * 개별 이모지 반응 칩 컴포넌트
 */
@Composable
private fun ReactionChip(
    emoji: String,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(
                color = SixpackTheme.colors.gray100,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = emoji,
            style = SixpackTheme.typography.b2Regular
        )
        Text(
            text = count.toString(),
            style = SixpackTheme.typography.c1Medium,
            color = SixpackTheme.colors.gray700
        )
    }
}
