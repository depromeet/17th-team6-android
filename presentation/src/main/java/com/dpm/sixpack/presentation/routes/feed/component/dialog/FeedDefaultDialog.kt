package com.dpm.sixpack.presentation.routes.feed.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun FeedDefaultDialog(
    title: String,
    subtitle: String,
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    confirmButtonText: String,
    onConfirmClick: () -> Unit,
    confirmButtonTextColor: Color,
    confirmButtonContainerColor: Color,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            shape = SixpackTheme.shapes.round20,
            color = SixpackTheme.colors.gray0,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // 제목
                Text(
                    text = title,
                    style = SixpackTheme.typography.t2Bold,
                    color = SixpackTheme.colors.gray900,
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 본문
                Text(
                    text = subtitle,
                    style = SixpackTheme.typography.b2Regular,
                    textAlign = TextAlign.Center,
                    color = SixpackTheme.colors.gray700,
                )

                Spacer(modifier = Modifier.height(20.dp))

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
                                containerColor = SixpackTheme.colors.gray100,
                            ),
                    ) {
                        Text(
                            text = "취소",
                            style = SixpackTheme.typography.b1Bold,
                            color = SixpackTheme.colors.gray700,
                        )
                    }

                    // 확인 버튼
                    DoRunDefaultButton(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .height(56.dp),
                        text = confirmButtonText,
                        onClick = onConfirmClick,
                        textColor = confirmButtonTextColor,
                        containerColor = confirmButtonContainerColor,
                    )
                }
            }
        }
    }
}
