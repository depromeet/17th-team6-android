package com.dpm.sixpack.presentation.routes.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun TextButtonWithIcon(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
            .padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray600
        )
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "목표 목록 화면으로 이동", // TODO: 접근성 resource 추가
            tint = SixpackTheme.colors.gray400,
        )
    }
}

@Preview
@Composable
private fun TextButtonWithIcon() {
    DoRunPreviewWrapper {
        TextButtonWithIcon(title = "목표 목록")
    }
}
