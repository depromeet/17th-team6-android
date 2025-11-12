package com.dpm.sixpack.presentation.routes.feed.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
fun CertificationCountView(
    users: List<PostingUserInfo>,
    isMeCertified : Boolean,
    onViewClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text =
        if (isMeCertified) {
            stringResource(id = R.string.feed_certification_count_view_others_certified, users.size)
        } else {
            stringResource(id = R.string.feed_certification_count_view_me_certified, users.size)
        }

    Row(
        modifier = modifier.noRippleClickable(onClick = onViewClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OverlappingProfiles(users = users)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = SixpackTheme.typography.b2Medium,
            color = SixpackTheme.colors.gray800,
        )
    }
}

@Composable
private fun OverlappingProfiles(
    users: List<PostingUserInfo>,
    modifier: Modifier = Modifier,
) {
    val visibleUsers = users.take(3).reversed()

    Box(modifier = modifier) {
        val profileCircleSize = 25.dp
        val overlap = 10.dp
        val offset = profileCircleSize - overlap

        visibleUsers.forEachIndexed { index, postingUser ->
            ProfileImageCircle(
                imageUrl = postingUser.user.profileImageUrl,
                modifier = Modifier.padding(start = 28.dp + (offset * (visibleUsers.size - 1 - index))),
            )
        }

        CountCircle(
            count = users.size,
            modifier = Modifier.padding(start = 0.dp),
        )
    }
}

@Composable
private fun CountCircle(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .background(SixpackTheme.colors.blue100, RoundedCornerShape(12.dp))
                .border(width = 1.dp, color = SixpackTheme.colors.gray0, shape = RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(id = R.string.feed_certification_count_view_count_label, count),
            color = SixpackTheme.colors.blue600,
            style = SixpackTheme.typography.b2Medium,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .padding(horizontal = 8.dp, vertical = 4.5.dp)
                    .widthIn(min = 22.dp),
        )
    }
}

@Composable
private fun ProfileImageCircle(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(25.dp)
                .clip(CircleShape)
                .border(width = 1.dp, color = SixpackTheme.colors.gray0, shape = CircleShape),
    ) {
        DoRunDefaultAsyncImage(
            model = imageUrl,
            contentDescription = stringResource(id = R.string.feed_post_user_info_profile_image_description),
            modifier =
                Modifier
                    .matchParentSize()
                    .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
    }
}

@Preview(name = "With Me Certified", showBackground = true)
@Composable
private fun CertificationCountViewWithMePreview() {
    DoRunPreviewWrapper {
        CertificationCountView(
            users =
                listOf(
                    PostingUserInfo(UserInfo(name = "User2", profileImageUrl = "")),
                    PostingUserInfo(UserInfo(name = "User2", profileImageUrl = "")),
                ),
            modifier = Modifier.padding(16.dp),
            onViewClick = {},
            isMeCertified = false,
            )
    }
}

@Preview(name = "Without Me Certified", showBackground = true)
@Composable
private fun CertificationCountViewWithoutMePreview() {
    DoRunPreviewWrapper {
        CertificationCountView(
            users =
                listOf(
                    PostingUserInfo(UserInfo(name = "User2", profileImageUrl = "")),
                    PostingUserInfo(UserInfo(name = "User2", profileImageUrl = "")),
                    PostingUserInfo(UserInfo(name = "User2", profileImageUrl = "")),
                    PostingUserInfo(UserInfo(name = "User2", profileImageUrl = "")),
                ),
            modifier = Modifier.padding(16.dp),
            isMeCertified = false,
            onViewClick = {},
        )
    }
}
