package com.dpm.sixpack.presentation.routes.friend.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.core.util.TimeUtil.isoStringToEpochSeconds
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultAsyncImage
import com.dpm.sixpack.presentation.common.model.FriendItem
import com.dpm.sixpack.presentation.common.util.convertTimeDiffToString
import com.dpm.sixpack.presentation.theme.SixpackTheme

private const val DROPDOWN_MENU_ITEM_HEIGHT_DP = 36

@Composable
internal fun FriendListItem(
    friend: FriendItem,
    isOptionMenuExpanded: Boolean,
    onOptionClick: () -> Unit,
    onOptionDismiss: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DoRunDefaultAsyncImage(
            model = friend.profileImgUrl,
            contentDescription = null,
            modifier =
                Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(1.dp, SixpackTheme.colors.gray200, shape = CircleShape),
            placeholder = ColorPainter(SixpackTheme.colors.gray50),
            error = painterResource(id = R.drawable.ill_profile_placeholder),
            contentScale = ContentScale.Crop,
        )

        Spacer(Modifier.width(12.dp))

        // 닉네임, 마지막 활동 시간
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            val secondsSinceLastRun =
                if (friend.lastRunInfo != null) {
                    val lastRunSec = isoStringToEpochSeconds(friend.lastRunInfo.lastestRunAt)
                    if (lastRunSec != null) (System.currentTimeMillis() / 1000L) - lastRunSec else null
                } else {
                    null // 러닝 기록 없음
                }
            Text(
                text = friend.nickName,
                style = SixpackTheme.typography.t2Bold,
                color = SixpackTheme.colors.gray900,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text =
                    if (secondsSinceLastRun != null) {
                        convertTimeDiffToString(
                            context = LocalContext.current,
                            secondsSinceLastRun,
                        )
                    } else {
                        ""
                    },
                style = SixpackTheme.typography.b2Regular,
                color = SixpackTheme.colors.gray500,
            )
        }

        // 더보기 버튼
        Box {
            IconButton(
                onClick = onOptionClick,
            ) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = null,
                    tint = Color.Gray,
                )
            }
            DropdownMenu(
                modifier = Modifier.background(Color.White),
                shape = SixpackTheme.shapes.round8,
                expanded = isOptionMenuExpanded,
                onDismissRequest = onOptionDismiss,
            ) {
                DropdownMenuItem(
                    modifier = Modifier.height(DROPDOWN_MENU_ITEM_HEIGHT_DP.dp),
                    text = {
                        Text(
                            text = stringResource(R.string.friend_delete),
                            style = SixpackTheme.typography.b2Regular,
                            color = SixpackTheme.colors.gray700,
                        )
                    },
                    onClick = {
                        // "삭제하기" 클릭 시
                        onOptionDismiss() // (메뉴를 닫고)
                        onDeleteClick() // (삭제 다이얼로그 띄우기 인텐트 전송)
                    },
                )
            }
        }
    }
}
