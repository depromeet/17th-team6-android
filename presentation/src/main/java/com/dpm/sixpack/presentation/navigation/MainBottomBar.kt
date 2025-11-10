package com.dpm.sixpack.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme
import kotlinx.collections.immutable.toPersistentList

@Composable
fun MainBottomBar(
    modifier: Modifier = Modifier,
    visible: Boolean,
    mainNavTabs: List<MainNavTab>,
    currentTab: MainNavTab?,
    onTabSelected: (MainNavTab) -> Unit,
) {
    val navigationAnimationSpec =
        tween<IntOffset>(
            durationMillis = 300,
            easing = LinearEasing,
        )
    val fadeAnimationSpec =
        tween<Float>(
            durationMillis = 300,
            easing = LinearEasing,
        )
    AnimatedVisibility(
        visible = visible,
        enter =
            slideInVertically(
                initialOffsetY = { it },
                animationSpec = navigationAnimationSpec,
            ) + fadeIn(fadeAnimationSpec),
        exit =
            slideOutVertically(
                targetOffsetY = { it },
                animationSpec = navigationAnimationSpec,
            ) + fadeOut(fadeAnimationSpec),
    ) {
        val borderColor = SixpackTheme.colors.gray50
        BottomAppBar(
            modifier =
                modifier
                    .drawBehind {
                        val strokeWidth = 1.dp.toPx()
                        drawLine(
                            color = borderColor,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = strokeWidth,
                        )
                    },
            contentColor = SixpackTheme.colors.gray0,
            containerColor = SixpackTheme.colors.gray0,
            contentPadding = PaddingValues(0.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                mainNavTabs.forEach { tab ->
                    BottomNavTabItem(
                        tab = tab,
                        isSelected = tab == currentTab,
                        onClick = { onTabSelected(tab) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun MainBottomBarPreview() {
    DoRunPreviewWrapper {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            MainBottomBar(
                visible = true,
                mainNavTabs = MainNavTab.entries.toPersistentList(),
                currentTab = MainNavTab.RUNNING,
                onTabSelected = { },
            )
        }
    }
}
