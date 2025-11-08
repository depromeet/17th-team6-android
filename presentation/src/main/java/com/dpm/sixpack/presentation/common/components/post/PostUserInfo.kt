package com.dpm.sixpack.presentation.common.components.post

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultAsyncImage
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 사용자 프로필 이미지, 이름, 게시 시간을 표시하는 재사용 가능한 핵심 컴포넌트.
 * 메뉴 버튼이 필요 없는 다른 화면에서도 사용할 수 있습니다.
 */
@Composable
fun PostUserInfo(
    userImageUrl: String,
    userName: String,
    postingTime: String,
    isMyPost: Boolean,
    onPostUserProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme : Boolean  = false,
) {
    val userNameTextColor = if(isDarkTheme) SixpackTheme.colors.gray0 else SixpackTheme.colors.gray900
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 사용자 프로필 이미지
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .border(width = 1.dp, color = SixpackTheme.colors.gray100, shape = CircleShape),
        ) {
            DoRunDefaultAsyncImage(
                model = userImageUrl,
                contentDescription = stringResource(id = R.string.feed_post_user_info_profile_image_description),
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .noRippleClickable(onClick = onPostUserProfileClick),
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = userName,
            style = SixpackTheme.typography.b1Medium,
            color = userNameTextColor,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.width(4.dp))

        if (isMyPost) {
            Surface(
                shape = CircleShape,
                color = SixpackTheme.colors.blue600,
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    text = stringResource(id = R.string.feed_post_user_info_my_post_badge),
                    color = SixpackTheme.colors.gray0,
                    style = SixpackTheme.typography.c1Medium,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = postingTime,
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray500,
        )
    }
}

@Preview(name = "My Post")
@Composable
fun PostUserInfoRowMyPostPreview() {
    DoRunPreviewWrapper {
        PostUserInfo(
            userImageUrl = "",
            userName = "비락식혜",
            postingTime = "36분 전",
            isMyPost = true,
            onPostUserProfileClick = {},
        )
    }
}

@Preview(name = "Other's Post")
@Composable
fun PostUserInfoRowOthersPostPreview() {
    DoRunPreviewWrapper {
        PostUserInfo(
            userImageUrl = "",
            userName = "다른 사용자",
            postingTime = "1시간 전",
            isMyPost = false,
            onPostUserProfileClick = {},
        )
    }
}
