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
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.PostingUserUiState
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun CertificationCountView(
    users: List<PostingUserUiState>,
    isMeCertified: Boolean,
    onViewClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (users.isEmpty()) return

    val text =
        if (isMeCertified) {
            stringResource(id = R.string.feed_certification_count_view_me_certified, users.size - 1)
        } else {
            stringResource(id = R.string.feed_certification_count_view_others_certified, users.size)
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
    users: List<PostingUserUiState>,
    modifier: Modifier = Modifier,
) {
    // 최대 3명의 프로필만 표시하고, 가장 최근에 인증한 사람이 가장 위에 보이도록 reversed()를 사용
    val visibleUsers = users.take(3).reversed()

    Box(modifier = modifier) {
        val profileCircleSize = 25.dp
        val overlap = 10.dp
        val offset = profileCircleSize - overlap

        visibleUsers.forEachIndexed { index, user ->
            ProfileImageCircle(
                imageUrl = user.userImageUrl,
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
        AsyncImage(
            model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
            contentDescription = stringResource(id = R.string.feed_post_user_info_profile_image_description),
            modifier =
                Modifier
                    .matchParentSize()
                    .clip(CircleShape),
            placeholder = ColorPainter(SixpackTheme.colors.gray200),
            error = ColorPainter(SixpackTheme.colors.gray200),
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
                    PostingUserUiState(userName = "User2", userImageUrl = ""),
                    PostingUserUiState(userName = "User3", userImageUrl = ""),
                ),
            isMeCertified = true,
            modifier = Modifier.padding(16.dp),
            onViewClick = {},
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
                    PostingUserUiState(userName = "User1", userImageUrl = ""),
                    PostingUserUiState(userName = "User2", userImageUrl = ""),
                    PostingUserUiState(userName = "User3", userImageUrl = ""),
                    PostingUserUiState(userName = "User4", userImageUrl = ""),
                ),
            isMeCertified = false,
            modifier = Modifier.padding(16.dp),
            onViewClick = {},
        )
    }
}
