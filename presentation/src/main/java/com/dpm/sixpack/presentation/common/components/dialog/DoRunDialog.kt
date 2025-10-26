package com.dpm.sixpack.presentation.common.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 공통 다이얼로그 컴포넌트 (Figma: popup)
 * 1개 버튼 또는 2개 버튼 스타일 지원
 *
 * @param title 다이얼로그 제목
 * @param description 다이얼로그 설명 텍스트
 * @param onDismiss 다이얼로그 닫기 콜백
 * @param primaryButtonText 주 버튼 텍스트 (필수)
 * @param primaryButtonOnClick 주 버튼 클릭 콜백
 * @param secondaryButtonText 보조 버튼 텍스트 (선택사항 - null이면 1 btn 스타일)
 * @param secondaryButtonOnClick 보조 버튼 클릭 콜백
 */
@Composable
fun CommonDialog(
    title: String,
    description: String,
    onDismiss: () -> Unit,
    primaryButtonText: String,
    primaryButtonOnClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryButtonText: String? = null,
    secondaryButtonOnClick: (() -> Unit)? = null,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
    ) {
        Column(
            modifier =
                modifier
                    .width(298.dp)
                    .background(
                        color = SixpackTheme.colors.gray0,
                        shape = RoundedCornerShape(16.dp),
                    ).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Title
            Text(
                text = title,
                style = SixpackTheme.typography.t2Bold,
                color = SixpackTheme.colors.gray900,
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = description,
                style = SixpackTheme.typography.b2Regular,
                color = SixpackTheme.colors.gray700,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Buttons
            if (secondaryButtonText != null) {
                // 2 버튼 스타일
                Row(
                    modifier = Modifier,
                ) {
                    DoRunDefaultButton(
                        text = secondaryButtonText,
                        onClick = {
                            secondaryButtonOnClick?.invoke()
                            onDismiss()
                        },
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(56.dp),
                        textColor = SixpackTheme.colors.gray900,
                        containerColor = SixpackTheme.colors.gray100,
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    DoRunDefaultButton(
                        text = primaryButtonText,
                        onClick = {
                            primaryButtonOnClick()
                            onDismiss()
                        },
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(56.dp),
                    )
                }
            } else {
                // 1 버튼 스타일
                DoRunDefaultButton(
                    text = primaryButtonText,
                    onClick = {
                        primaryButtonOnClick()
                        onDismiss()
                    },
                    modifier =
                        Modifier
                            .width(258.dp)
                            .height(56.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun CommonDialogOneButtonPreview() {
    DoRunPreviewWrapper {
        CommonDialog(
            title = "다이얼로그 제목",
            description = "다이얼로그 설명 텍스트가 여러 줄로 표시될 수 있습니다.",
            onDismiss = {},
            primaryButtonText = "확인",
            primaryButtonOnClick = {},
        )
    }
}

@Preview
@Composable
private fun CommonDialogTwoButtonPreview() {
    DoRunPreviewWrapper {
        CommonDialog(
            title = "다이얼로그 제목",
            description = "다이얼로그 설명 텍스트가 여러 줄로 표시될 수 있습니다.",
            onDismiss = {},
            primaryButtonText = "확인",
            primaryButtonOnClick = {},
            secondaryButtonText = "취소",
            secondaryButtonOnClick = {},
        )
    }
}
