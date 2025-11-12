package com.dpm.sixpack.presentation.routes.running.session.component.ready

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun BaseReadyOverlay(
    primaryText: String,
    countdown: Int,
    onlyText: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(
                color = Color.Black.copy(alpha = 0.6f),
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            if (onlyText) {
                Image(
                    painter = painterResource(id = R.drawable.ill_loading_overlay),
                    contentDescription = "Loading Overlay",
                )
            }
            Text(
                text = primaryText,
                style = SixpackTheme.typography.h4Bold,
                color = SixpackTheme.colors.gray0,
            )

            if (!onlyText) {
                CircularLoadingIndicator(
                    modifier = Modifier.padding(top = 12.dp, bottom = 100.dp),
                    countdown = countdown,
                )
            }
        }
    }
}

@Composable
private fun CircularLoadingIndicator(
    countdown: Int,
    modifier: Modifier = Modifier,
) {
    val animatable = remember(countdown) { Animatable(0f) }

    LaunchedEffect(countdown) {
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        )
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier =
                Modifier
                    .size(300.dp),
            progress = { animatable.value },
            strokeWidth = 10.dp,
            color = SixpackTheme.colors.gray0,
            trackColor = SixpackTheme.colors.gray900,
        )
        // TODO KS: Apply Font Style
        Text(
            text = countdown.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 96.sp,
            color = SixpackTheme.colors.gray0,
        )
    }
}

@Preview
@Composable
private fun PreviewReadyLoadingScreenNoCount() {
    BaseReadyOverlay(
        primaryText = "잠시 후 러닝 시작",
        countdown = 5,
        onlyText = true,
    )
}

@Preview
@Composable
private fun PreviewReadyLoadingScreenWithCount() {
    BaseReadyOverlay(
        primaryText = "잠시 후 러닝 시작",
        countdown = 2,
        onlyText = false,
    )
}
