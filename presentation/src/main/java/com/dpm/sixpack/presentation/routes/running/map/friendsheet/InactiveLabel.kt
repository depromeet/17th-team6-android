package com.dpm.sixpack.presentation.routes.running.map.friendsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun InactiveLabel(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.inactive_label_text),
        modifier =
            modifier
                .background(Color(0xFF717171), CircleShape)
                .padding(horizontal = 6.dp, vertical = 2.dp),
        color = SixpackTheme.colors.gray0,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
    )
}
