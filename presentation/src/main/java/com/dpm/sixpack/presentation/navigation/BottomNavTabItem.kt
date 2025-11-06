package com.dpm.sixpack.presentation.navigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun RowScope.BottomNavTabItem(
    modifier: Modifier = Modifier,
    tab: MainNavTab,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier =
            modifier
                .weight(1f)
                .fillMaxHeight()
                .selectable(
                    selected = isSelected,
                    indication = null,
                    role = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val selectedColor = SixpackTheme.colors.gray900
        val unselectedColor = SixpackTheme.colors.gray300

        Spacer(modifier = Modifier.height(8.dp))

        Icon(
            painter = painterResource(id = tab.iconResId),
            contentDescription = stringResource(tab.titleResId),
            tint = if (isSelected) selectedColor else unselectedColor,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = stringResource(tab.titleResId),
            color = if (isSelected) selectedColor else unselectedColor,
            style = SixpackTheme.typography.c1Regular,
        )
    }
}
