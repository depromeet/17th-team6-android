package com.dpm.sixpack.presentation.routes.session.component.panel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun RecordItem(
    label: String,
    recordValue: String,
) {
    Column(horizontalAlignment = Alignment.Companion.Start) {
        Text(
            text = label,
            style = SixpackTheme.typography.b2Medium,
            color = SixpackTheme.colors.gray500,
        )
        Spacer(modifier = Modifier.Companion.height(4.dp))

        Text(
            text = recordValue,
            style = SixpackTheme.typography.h2Bold,
            color = SixpackTheme.colors.gray900,
        )
    }
}

@Preview
@Composable
private fun PreviewRecordItem() {
    RecordItem(
        label = "현재 거리",
        recordValue = "1.5km",
    )
}
