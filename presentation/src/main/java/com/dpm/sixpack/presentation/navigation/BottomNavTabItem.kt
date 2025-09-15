package com.dpm.sixpack.presentation.navigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dpm.sixpack.presentation.theme.LocalSixpackColors
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun RowScope.BottomNavTabItem(
    modifier: Modifier = Modifier,
    tab: MainNavTab,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
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
        // FIXME SK: ColorScheme
        val selectedColor = LocalSixpackColors.current.primary
        val unselectedColor = Color(0xFFCBD0DA)

        Spacer(modifier = Modifier.height(8.dp))

        Icon(
            painter = painterResource(id = tab.iconResId),
            contentDescription = tab.contentDescription,
            tint = if (isSelected) selectedColor else unselectedColor,
        )

        Text(
            text = tab.contentDescription,
            color = if (isSelected) selectedColor else unselectedColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
