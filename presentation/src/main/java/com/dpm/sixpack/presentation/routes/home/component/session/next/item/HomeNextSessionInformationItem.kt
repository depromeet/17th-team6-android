package com.dpm.sixpack.presentation.routes.home.component.session.next.item

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
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
fun HomeNextSessionInformationItem(
    @DrawableRes iconRes: Int,
    value: String,
    title: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = "",
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription, // TODO: 접근성 resource
            tint = SixpackTheme.colors.gray300
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = value,
            style = SixpackTheme.typography.t1Bold,
            color = SixpackTheme.colors.gray900
        )
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = title,
            style = SixpackTheme.typography.c1Regular,
            color = SixpackTheme.colors.gray600
        )
    }
}

@Preview
@Composable
private fun HomeNextSessionInformationItemPreview() {
    DoRunPreviewWrapper {
        HomeNextSessionInformationItem(
            iconRes = R.drawable.ic_distance,
            value = "5.0km",
            title = "목표 거리"
        )
    }
}
