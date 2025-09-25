package com.dpm.sixpack.presentation.routes.home.component.total.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun HomeTotalGoalInformationItem(
    modifier: Modifier = Modifier,
    value: String,
    title: String,
) {
    Column(
        modifier = modifier.wrapContentSize(),
    ) {
        Text(
            text = value,
            style = SixpackTheme.typography.h2Bold,
            color = SixpackTheme.colors.gray900,
        )
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = title,
            style = SixpackTheme.typography.c1Regular,
            color = SixpackTheme.colors.gray600,
        )
    }
}

@Preview
@Composable
private fun HomeTotalGoalInformationItemPreview() {
    DoRunPreviewWrapper {
        HomeTotalGoalInformationItem(
            value = "5.0km",
            title = "목표 거리",
        )
    }
}
