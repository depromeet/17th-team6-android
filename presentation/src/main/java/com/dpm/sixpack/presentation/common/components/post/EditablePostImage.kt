package com.dpm.sixpack.presentation.common.components.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.model.RunningSummary
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 수정 가능한 게시물 이미지 컴포넌트
 * 이미지와 배경사진 변경 버튼을 포함합니다.
 *
 * @param postImageUrl 표시할 이미지 URL (원본 또는 선택된 이미지 URI)
 * @param runningSummary 러닝 정보 요약
 * @param onImageEditClick 배경사진 변경 버튼 클릭 콜백
 * @param onPostImageClick 게시물 이미지 클릭 콜백
 * @param modifier Modifier
 * @param buttonText 버튼 텍스트 (기본값: "배경사진 변경")
 * @param enabled 버튼 활성화 상태 (기본값: true)
 */
@Composable
fun EditablePostImage(
    postImageUrl: String,
    runningSummary: RunningSummary,
    onImageEditClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPostImageClick: () -> Unit = {},
    buttonText: String = "배경사진 변경",
    enabled: Boolean = true,
) {
    Column(modifier = modifier) {
        // 게시물 이미지
        PostImageWithRecord(
            postImageUrl = postImageUrl,
            runningSummary = runningSummary,
            onPostImageClick = onPostImageClick,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 배경사진 변경 버튼
        DoRunDefaultButton(
            text = buttonText,
            onClick = onImageEditClick,
            modifier = Modifier.fillMaxWidth(),
            textColor = SixpackTheme.colors.blue600,
            containerColor = SixpackTheme.colors.blue200,
            enabled = enabled,
        )
    }
}
