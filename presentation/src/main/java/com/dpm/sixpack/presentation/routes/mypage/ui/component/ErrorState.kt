package com.dpm.sixpack.presentation.routes.mypage.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ill_warning),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(120.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "오류가 발생했습니다",
                style = SixpackTheme.typography.t2Bold,
                color = SixpackTheme.colors.gray900,
                textAlign = TextAlign.Center,
            )
            Text(
                text = message,
                style = SixpackTheme.typography.b2Regular,
                color = SixpackTheme.colors.gray700,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onRetry) {
            Text(
                text = "다시 시도",
                style = SixpackTheme.typography.b1Medium,
                color = SixpackTheme.colors.blue600,
            )
        }
    }
}

@Preview
@Composable
private fun ErrorStatePreview() {
    DoRunPreviewWrapper {
        ErrorState(
            message = "네트워크 연결을 확인해주세요",
            onRetry = {},
        )
    }
}
