package com.dpm.sixpack.presentation.routes.session.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun RunningDialog(
    title: String,
    contentText: String,
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    confirmButton: @Composable RowScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties =
            DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
    ) {
        Surface(
            shape = SixpackTheme.shapes.round20,
            color = SixpackTheme.colors.gray0,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // 제목
                Text(
                    text = title,
                    style = SixpackTheme.typography.t1Bold,
                    color = SixpackTheme.colors.gray900,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 본문
                Text(
                    text = contentText,
                    style = SixpackTheme.typography.b1Medium,
                    textAlign = TextAlign.Center,
                    color = SixpackTheme.colors.gray700,
                    lineHeight = 24.sp,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 버튼
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // 취소 버튼
                    Button(
                        onClick = onCancelClick,
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(56.dp),
                        shape = SixpackTheme.shapes.round12,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF0F0F0), // 회색 배경
                            ),
                    ) {
                        Text(
                            text = "취소",
                            style = SixpackTheme.typography.b1Bold,
                            color = SixpackTheme.colors.gray700,
                        )
                    }

                    // Confirm 버튼
                    confirmButton()
                }
            }
        }
    }
}

@Preview
@Composable
fun SkipWarmupDialogPreview() {
    RunningDialog(
        onDismissRequest = {},
        onCancelClick = {},
        title = "웜업 건너뛰기",
        contentText = "안전한 러닝을 위해 웜업을 권장해요.\n건너뛰고 본 러닝을 시작할까요?",
    ) {
        DoRunDefaultButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(56.dp),
            text = stringResource(R.string.panel_skip),
            onClick = { },
        )
    }
}
