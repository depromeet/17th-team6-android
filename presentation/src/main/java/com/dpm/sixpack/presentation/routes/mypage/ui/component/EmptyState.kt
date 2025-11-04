package com.dpm.sixpack.presentation.routes.mypage.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ill_empty_2),
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
                text = title,
                style = SixpackTheme.typography.t2Bold,
                color = SixpackTheme.colors.gray900,
                textAlign = TextAlign.Center,
            )
            Text(
                text = description,
                style = SixpackTheme.typography.b2Regular,
                color = SixpackTheme.colors.gray700,
                textAlign = TextAlign.Center,
            )
        }
    }
}
