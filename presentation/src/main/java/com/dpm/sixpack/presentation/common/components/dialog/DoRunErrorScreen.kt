package com.dpm.sixpack.presentation.common.components.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun DoRunErrorScreen(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    confirmButtonText: String,
    onConfirmClick: () -> Unit,
) {
    Box(
        modifier =
            modifier.background(
                color = SixpackTheme.colors.gray0,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_error),
                contentDescription = "Error",
                modifier = Modifier.size(100.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = SixpackTheme.typography.t2Bold,
                color = SixpackTheme.colors.gray900,
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                        .fillMaxWidth(fraction = 0.33f)
                        .height(56.dp),
                text = confirmButtonText,
                onClick = onConfirmClick,
                textColor = SixpackTheme.colors.gray0,
                containerColor = SixpackTheme.colors.blue600,
            )
        }
    }
}

@Preview
@Composable
private fun DoRunErrorScreenPreview() {
    DoRunPreviewWrapper {
        DoRunErrorScreen(
            modifier = Modifier.fillMaxSize(),
            title = "네트워크 연결이 불안정해요",
            subtitle = "연결상태를 확인한 뒤 다시 시도해주세요",
            confirmButtonText = "확인",
            onConfirmClick = {},
        )
    }
}
