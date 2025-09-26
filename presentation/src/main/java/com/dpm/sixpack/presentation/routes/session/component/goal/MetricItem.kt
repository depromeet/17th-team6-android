package com.dpm.sixpack.presentation.routes.session.component.goal

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
internal fun MetricItem(
    value: String,
    label: String,
    @DrawableRes imageResId: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(imageResId),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = SixpackTheme.typography.t1Bold,
            color = SixpackTheme.colors.gray900,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = SixpackTheme.typography.c1Regular,
            color = SixpackTheme.colors.gray600,
        )
    }
}

@Preview
@Composable
internal fun MetricItemPreview() {
    MetricItem(value = "10km", label = "목표 거리", imageResId = R.drawable.ic_pace)
}
