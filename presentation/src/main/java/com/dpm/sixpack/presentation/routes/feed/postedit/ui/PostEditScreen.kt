package com.dpm.sixpack.presentation.routes.feed.postedit.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.post.PostImageWithRecord
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.common.model.RunningSummary
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.routes.feed.postedit.contract.PostEditUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun PostEditScreen(
    state: PostEditUiState,
    modifier: Modifier = Modifier,
    onBackButtonClick: () -> Unit = {},
    onSaveIconClick: () -> Unit = {},
    onImageEditButtonClick: () -> Unit = {},
    onSubmitClick: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxSize(),
    ) {
        // TopBar
        DoRunNavigationTopBar(
            navigateToBack = onBackButtonClick,
            trailingIcon = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier
                            .size(44.dp)
                            .noRippleClickable(onClick = onSaveIconClick),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_save),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = SixpackTheme.colors.gray800,
                    )
                }
            },
            modifier = Modifier.padding(horizontal = 10.dp),
        )

        // Content
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = SixpackTheme.colors.blue600,
                )
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 24.dp),
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // 게시물 이미지
                val imageUrl =
                    state.selectedImageUri?.toString() ?: state.originalPost.postImageUrl

                PostImageWithRecord(
                    postImageUrl = imageUrl,
                    runningSummary = state.originalPost.runningInfo,
                    onPostImageClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(12.dp))

                DoRunDefaultButton(
                    text = "배경사진 변경",
                    onClick = onImageEditButtonClick,
                    modifier = Modifier.fillMaxWidth(),
                    textColor = SixpackTheme.colors.blue600,
                    containerColor = SixpackTheme.colors.blue200,
                )

                Spacer(Modifier.weight(1f))

                DoRunDefaultButton(
                    text = "수정하기",
                    onClick = onSubmitClick,
                    modifier = Modifier.fillMaxWidth(),
                    textColor = SixpackTheme.colors.gray0,
                    containerColor = SixpackTheme.colors.blue600,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostEditScreenPreview() {
    val dummyPost =
        PostResource(
            feedId = 1L,
            postImageUrl = "",
            runningInfo =
                RunningSummary(
                    totalDistance = "8.02km",
                    totalTime = "1:52:06",
                    averagePace = "7'30\"",
                    cadence = "144",
                    recordDateTime = "2025.10.12 · 오전 12분",
                ),
        )

    val dummyState =
        PostEditUiState(
            originalPost = dummyPost,
            hasImagePermission = true,
        )

    DoRunPreviewWrapper {
        PostEditScreen(
            state = dummyState,
        )
    }
}
