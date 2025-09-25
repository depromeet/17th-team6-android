package com.dpm.sixpack.presentation.routes.session_list.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun SessionListErrorSnackBar(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    title: String
) {
    Row(
        modifier = modifier
            .wrapContentSize()
            .background(
                color = SixpackTheme.colors.gray900.copy(alpha = 0.73f),
                shape = SixpackTheme.shapes.round12
            )
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = ""
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = title,
            style = SixpackTheme.typography.b1Medium,
            color = SixpackTheme.colors.gray0,
        )
    }
}

@Preview
@Composable
private fun SessionListErrorSnackBarPreview() {
    SessionListErrorSnackBar(
        iconRes = R.drawable.ill_warning,
        title = "이전 회차를 끝내야 도전할 수 있어요."
    )
}
