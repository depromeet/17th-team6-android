package com.dpm.sixpack.presentation.routes.feed.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.PostingUserUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun CertificationCountView(
    users: List<PostingUserUiState>,
    isMeCertified: Boolean,
    modifier: Modifier = Modifier
) {
    if (users.isEmpty()) return

    val text = if (isMeCertified) {
        "'나'를 제외한 ${users.size}명이 인증했어요!"
    } else {
        "${users.size}명이 인증했어요!"
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OverlappingProfiles(users = users)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = SixpackTheme.typography.b2Medium,
            color = SixpackTheme.colors.gray800
        )
    }
}

@Composable
private fun OverlappingProfiles(
    users: List<PostingUserUiState>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val profileCircleSize = 25.dp
        val overlap = 10.dp
        val offset = profileCircleSize - overlap

        ProfileImageCircle(
            imageUrl = users[2].userImageUrl,
            modifier = Modifier.padding(start = 28.dp + offset * 2)
        )

        ProfileImageCircle(
            imageUrl = users[1].userImageUrl,
            modifier = Modifier.padding(start = 28.dp + offset)
        )

        ProfileImageCircle(
            imageUrl = users[0].userImageUrl,
            modifier = Modifier.padding(start = 28.dp)
        )

        CountCircle(
            count = users.size,
            modifier = Modifier.padding(start = 0.dp)
        )
    }
}

@Composable
private fun CountCircle(
    count: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(SixpackTheme.colors.blue100, RoundedCornerShape(12.dp))
            .border(width = 1.dp, color = SixpackTheme.colors.gray0, shape = RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+$count",
            color = SixpackTheme.colors.blue600,
            style = SixpackTheme.typography.b2Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.5.dp)
                .widthIn(min = 22.dp)

        )
    }
}

@Composable
private fun ProfileImageCircle(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(25.dp)
            .clip(CircleShape)
            .border(width = 1.dp, color = SixpackTheme.colors.gray0, shape = CircleShape)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "User profile image",
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape),
            placeholder = ColorPainter(SixpackTheme.colors.gray200),
            error = ColorPainter(SixpackTheme.colors.gray200),
            contentScale = ContentScale.Crop
        )
    }
}


@Preview(name = "With Me Certified", showBackground = true)
@Composable
private fun CertificationCountViewWithMePreview() {
    DoRunPreviewWrapper {
        CertificationCountView(
            users = listOf(
                PostingUserUiState(userName = "User1", userImageUrl = ""),
                PostingUserUiState(userName = "User2", userImageUrl = ""),
                PostingUserUiState(userName = "User3", userImageUrl = ""),
            ),
            isMeCertified = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Without Me Certified", showBackground = true)
@Composable
private fun CertificationCountViewWithoutMePreview() {
    DoRunPreviewWrapper {
        CertificationCountView(
            users = listOf(
                PostingUserUiState(userName = "User1", userImageUrl = ""),
                PostingUserUiState(userName = "User2", userImageUrl = ""),
                PostingUserUiState(userName = "User3", userImageUrl = ""),
                PostingUserUiState(userName = "User4", userImageUrl = ""),
            ),
            isMeCertified = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}
