package com.dpm.sixpack.presentation.routes.postupload.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.post.EditablePostImage
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.common.model.RunningSummary
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.routes.postupload.contract.PostUploadUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun PostUploadScreen(
    state: PostUploadUiState,
    modifier: Modifier = Modifier,
    onBackButtonClick: () -> Unit = {},
    onSaveIconClick: () -> Unit = {},
    onImageEditButtonClick: () -> Unit = {},
    onUploadButtonClick: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxSize(),
    ) {
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
            modifier = Modifier,
        )
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            val imageUrl =
                state.selectedImageUri?.toString() ?: state.mapImageUrl

            EditablePostImage(
                postImageUrl = imageUrl,
                runningSummary = state.runningSummary,
                onImageEditClick = onImageEditButtonClick,
                buttonText = stringResource(id = R.string.feed_post_edit_change_background_button),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.weight(1f))

            DoRunDefaultButton(
                text = stringResource(id = R.string.feed_post_upload_submit_button),
                onClick = onUploadButtonClick,
                modifier = Modifier.fillMaxWidth(),
                textColor = SixpackTheme.colors.gray0,
                containerColor = SixpackTheme.colors.blue600,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostUploadScreenPreview() {
    val dummyState =
        PostUploadUiState(
            sessionId = 1,
            runningSummary =
                RunningSummary(
                    totalDistance = "8.02km",
                    totalTime = "1:52:06",
                    averagePace = "7'30\"",
                    cadence = "144",
                    recordDateTime = "2025.10.12 · 오전 12분",
                ),
            hasImagePermission = true,
        )

    DoRunPreviewWrapper {
        PostUploadScreen(
            state = dummyState,
        )
    }
}
