package com.dpm.sixpack.presentation.routes.onboarding.ui.component.pager.item

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun OnboardingPagerIndicator(
    size: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    val indicatorHeight = 8.dp
    val selectedWidth = 16.dp
    val unselectedWidth = 8.dp
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterHorizontally)
    ) {
        for (i in 0 until size) {
            val selected = i == currentPage

            val targetWidth = if (selected) selectedWidth else unselectedWidth
            val animatedWidth by animateDpAsState(targetValue = targetWidth)

            val targetColor = if (selected) SixpackTheme.colors.blue600 else SixpackTheme.colors.gray200
            val animatedColor by animateColorAsState(targetValue = targetColor)

            Box(
                modifier = Modifier
                    .width(animatedWidth)
                    .height(indicatorHeight)
                    .background(color = animatedColor, shape = RoundedCornerShape(percent = 50))
            )
        }
    }
}

