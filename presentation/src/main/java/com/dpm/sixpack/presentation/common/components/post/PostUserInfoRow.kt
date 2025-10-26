package com.dpm.sixpack.presentation.common.components.post

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 포스트 상단의 사용자 정보를 표시하는 컴포넌트
 */
@Composable
fun PostUserInfoRow(
    userImageUrl: String,
    userName: String,
    postTime: String,
    isMyPost: Boolean,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 사용자 프로필 이미지
        Box(
            modifier = Modifier
                .size(32.dp)
                .border(width = 1.dp, color = SixpackTheme.colors.gray100, shape = CircleShape)

        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(userImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "User profile image",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                placeholder = ColorPainter(SixpackTheme.colors.gray0),
                error = ColorPainter(SixpackTheme.colors.gray200),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 사용자 이름 및 게시 시간
        Text(
            text = userName,
            style = SixpackTheme.typography.b1Medium,
            color = SixpackTheme.colors.gray900,
            overflow = TextOverflow.Companion.Ellipsis
        )

        Spacer(modifier = Modifier.width(4.dp))

        if (isMyPost) {
            Surface(
                shape = CircleShape,
                color = SixpackTheme.colors.blue600,
            ) {
                Text(
                    modifier = Modifier.Companion.padding(horizontal = 6.dp, vertical = 2.dp),
                    text = "나",
                    color = SixpackTheme.colors.gray0,
                    style = SixpackTheme.typography.c1Medium,
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = postTime,
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray500,
        )

        Spacer(modifier = Modifier.weight(1f))

        // 메뉴 버튼
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_meatball_menu),
                contentDescription = "Post options menu",
                tint = SixpackTheme.colors.gray600
            )
        }
    }
}

@Preview(name = "My Post")
@Composable
fun PostUserInfoRowMyPostPreview() {
    DoRunPreviewWrapper {
        PostUserInfoRow(
            userImageUrl = "",
            userName = "비락식혜",
            postTime = "36분 전",
            isMyPost = true,
            onMenuClick = {}
        )
    }
}

@Preview(name = "Other's Post")
@Composable
fun PostUserInfoRowOthersPostPreview() {
    DoRunPreviewWrapper {
        PostUserInfoRow(
            userImageUrl = "",
            userName = "다른 사용자",
            postTime = "1시간 전",
            isMyPost = false,
            onMenuClick = {}
        )
    }
}
