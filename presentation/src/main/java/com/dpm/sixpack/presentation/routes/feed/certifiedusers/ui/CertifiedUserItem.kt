package com.dpm.sixpack.presentation.routes.feed.certifiedusers.ui

import android.R.attr.name
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultAsyncImage
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.PostingUserInfo
import com.dpm.sixpack.presentation.common.model.UserInfo
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun CertifiedUserItem(
    name: String,
    profileImgUrl: String,
    isMe: Boolean,
    postingTime: String,
    onUserClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .noRippleClickable(onClick = onUserClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(52.dp)
                    .border(width = 1.dp, color = SixpackTheme.colors.gray100, shape = CircleShape),
        ) {
            DoRunDefaultAsyncImage(
                model = profileImgUrl,
                contentDescription = stringResource(id = R.string.feed_post_user_info_profile_image_description),
                modifier =
                    Modifier
                        .size(52.dp)
                        .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 사용자 이름
        Text(
            text = name,
            style = SixpackTheme.typography.t2Bold,
            color = SixpackTheme.colors.gray900,
        )

        Spacer(modifier = Modifier.width(4.dp))

        // "나" 뱃지
        if (isMe) {
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

        // 인증 시간
        Text(
            text = postingTime,
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray500,
        )
    }
}

@Preview
@Composable
private fun CertifiedUserItemPreview() {
    DoRunPreviewWrapper {
        CertifiedUserItem(
            name = "비락식혜",
            profileImgUrl = "",
            isMe = true,
        postingTime = "2일 전",
        onUserClick = {},
        )
    }
}
