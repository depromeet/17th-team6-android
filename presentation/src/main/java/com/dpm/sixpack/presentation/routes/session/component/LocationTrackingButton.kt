package com.dpm.sixpack.presentation.routes.session.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun LocationTrackingButton(
    isFollowing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    Box(
        modifier =
            modifier
                .size(44.dp)
                .shadow(elevation = 6.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(
                    onClick = onClick,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = Color.Gray),
                ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_tracking),
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint =
                if (isFollowing) {
                    SixpackTheme.colors.blue600
                } else {
                    SixpackTheme.colors.gray400
                },
        )
    }
}

// --- Preview ---

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
fun LocationTrackingButtonPreview() {
    LocationTrackingButton(
        isFollowing = true,
        onClick = {},
    )
}
