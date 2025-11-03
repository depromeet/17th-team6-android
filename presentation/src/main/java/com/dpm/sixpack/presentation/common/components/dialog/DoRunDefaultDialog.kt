package com.dpm.sixpack.presentation.common.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 기본형 다이얼로그 (버튼 1개 버전)
 */
@Composable
fun DoRunDefaultDialog(
    title: String,
    subtitle: String,
    confirmButtonText: String,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            shape = SixpackTheme.shapes.round20,
            color = SixpackTheme.colors.gray0,
            modifier = modifier,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = SixpackTheme.typography.t2Bold,
                    color = SixpackTheme.colors.gray900,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = SixpackTheme.typography.b2Regular,
                    textAlign = TextAlign.Center,
                    color = SixpackTheme.colors.gray700,
                )

                Spacer(modifier = Modifier.height(20.dp))

                DoRunDefaultButton(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    text = confirmButtonText,
                    onClick = onConfirmClick,
                    textColor = SixpackTheme.colors.gray0,
                    containerColor = SixpackTheme.colors.blue600,
                )
            }
        }
    }
}

/**
 * 기본형 다이얼로그 (버튼 2개 버전)
 */

enum class DialogButtonType {
    /** 일반적인 확인 (파란색) */
    Primary,

    /** 삭제 등 파괴적인 작업 (빨간색) */
    Destructive,
}

@Composable
fun DoRunDefaultDialog(
    title: String,
    subtitle: String,
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    cancelButtonText: String,
    confirmButtonText: String,
    onConfirmClick: () -> Unit,
    confirmButtonType: DialogButtonType,
    modifier: Modifier = Modifier,
) {
    val (confirmTextColor, confirmContainerColor) =
        when (confirmButtonType) {
            DialogButtonType.Primary ->
                Pair(
                    SixpackTheme.colors.gray0,
                    SixpackTheme.colors.blue600,
                )

            DialogButtonType.Destructive ->
                Pair(
                    SixpackTheme.colors.red,
                    SixpackTheme.colors.redLight,
                )
        }
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            shape = SixpackTheme.shapes.round20,
            color = SixpackTheme.colors.gray0,
            modifier = modifier,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = SixpackTheme.typography.t2Bold,
                    color = SixpackTheme.colors.gray900,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = SixpackTheme.typography.b2Regular,
                    textAlign = TextAlign.Center,
                    color = SixpackTheme.colors.gray700,
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    DoRunDefaultButton(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .height(56.dp),
                        text = cancelButtonText,
                        onClick = onCancelClick,
                        textColor = SixpackTheme.colors.gray900,
                        containerColor = SixpackTheme.colors.gray100,
                    )

                    DoRunDefaultButton(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .height(56.dp),
                        text = confirmButtonText,
                        onClick = onConfirmClick,
                        textColor = confirmTextColor,
                        containerColor = confirmContainerColor,
                    )
                }
            }
        }
    }
}

@Preview(name = "Single Button Dialog")
@Composable
private fun DoRunDefaultDialogOneButtonPreview() {
    DoRunPreviewWrapper {
        DoRunDefaultDialog(
            title = "제목",
            subtitle = "내용",
            onDismissRequest = {},
            confirmButtonText = "확인",
            onConfirmClick = {},
        )
    }
}

@Preview(name = "Two Buttons Dialog Primary")
@Composable
private fun DoRunDefaultDialogTwoButtonsPreview() {
    DoRunPreviewWrapper {
        DoRunDefaultDialog(
            title = "제목",
            subtitle = "내용",
            onDismissRequest = {},
            onCancelClick = {},
            cancelButtonText = "취소",
            confirmButtonText = "확인",
            onConfirmClick = {},
            confirmButtonType = DialogButtonType.Primary,
        )
    }
}

@Preview(name = "Two Buttons Dialog Destructive")
@Composable
private fun DoRunDefaultDialogTwoButtonsPreview2() {
    DoRunPreviewWrapper {
        DoRunDefaultDialog(
            title = "제목",
            subtitle = "내용",
            onDismissRequest = {},
            onCancelClick = {},
            cancelButtonText = "취소",
            confirmButtonText = "확인",
            onConfirmClick = {},
            confirmButtonType = DialogButtonType.Destructive,
        )
    }
}
