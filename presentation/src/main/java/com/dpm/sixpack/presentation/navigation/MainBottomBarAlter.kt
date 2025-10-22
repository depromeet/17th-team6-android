package com.dpm.sixpack.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.theme.SixpackTheme
import kotlinx.collections.immutable.toPersistentList

@Composable
fun MainBottomBarAlter(
    modifier: Modifier = Modifier,
    visible: Boolean,
    mainNavTabs: List<MainNavTab>,
    currentTab: MainNavTab?,
    onTabSelected: (MainNavTab) -> Unit,
    onFabClick: () -> Unit = {},
) {
    val showFloating = currentTab != MainNavTab.MY_PAGE

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        BottomAppBar(
            modifier = modifier,
            contentColor = SixpackTheme.colors.gray0,
            containerColor = SixpackTheme.colors.gray0,
            contentPadding = PaddingValues(0.dp),
        ) {
            Row(
                modifier =
                    modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = SixpackTheme.colors.gray50,
                        ).background(color = SixpackTheme.colors.gray0),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                mainNavTabs.forEachIndexed { idx, tab ->
                    BottomNavTabItem(
                        tab = tab,
                        isSelected = tab == currentTab,
                        onClick = { onTabSelected(tab) },
                    )

                    if (showFloating && idx == mainNavTabs.size / 2 - 1) {
                        val imageVector =
                            when (currentTab) {
                                MainNavTab.RUNNING -> Icons.AutoMirrored.Filled.DirectionsRun
                                else -> Icons.Default.Add
                            }
                        FloatingActionButton(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            onClick = onFabClick,
                            shape = CircleShape,
                            containerColor = SixpackTheme.colors.blue600,
                        ) {
                            Icon(
                                imageVector = imageVector,
                                contentDescription = "중앙버튼",
                                tint = SixpackTheme.colors.gray0,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MainBottomBarAlterPreviewRunning() {
    MaterialTheme {
        MainBottomBarAlter(
            visible = true,
            mainNavTabs = MainNavTab.entries.toPersistentList(),
            currentTab = MainNavTab.RUNNING,
            onTabSelected = { },
        )
    }
}

@Preview
@Composable
private fun MainBottomBarAlterPreviewFeed() {
    MaterialTheme {
        MainBottomBarAlter(
            visible = true,
            mainNavTabs = MainNavTab.entries.toPersistentList(),
            currentTab = MainNavTab.FEED,
            onTabSelected = { },
        )
    }
}

@Preview
@Composable
private fun MainBottomBarAlterPreviewMyPage() {
    MaterialTheme {
        MainBottomBarAlter(
            visible = true,
            mainNavTabs = MainNavTab.entries.toPersistentList(),
            currentTab = MainNavTab.MY_PAGE,
            onTabSelected = { },
        )
    }
}
