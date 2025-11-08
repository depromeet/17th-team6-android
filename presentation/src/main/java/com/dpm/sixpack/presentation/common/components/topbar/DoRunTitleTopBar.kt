package com.dpm.sixpack.presentation.common.components.topbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun DoRunNavigationTopBar(
    navigateToBack: () -> Unit,
    modifier: Modifier = Modifier,
    titleContent: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isDarkTheme: Boolean = false,
) {
    DoRunTopBarSlot(
        modifier = modifier,
        leadingContent = {
            NavigateBackButton(
                onClick = navigateToBack,
                isDarkTheme = isDarkTheme,
            )
        },
        content = titleContent,
        trailingContent = trailingIcon,
    )
}

/**
 * 타이틀이 있는 TopBar
 * DoRunNavigationTopBar의 래퍼 함수
 */
@Composable
fun DoRunTitleTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    DoRunNavigationTopBar(
        navigateToBack = onBackClick,
        modifier = modifier,
        titleContent = {
            Text(
                text = title,
                style = SixpackTheme.typography.t1Bold,
                color = SixpackTheme.colors.gray900,
            )
        },
        trailingIcon = trailingIcon,
    )
}

@Composable
fun NavigateBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .size(44.dp)
                .noRippleClickable(onClick = onClick),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_left),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (isDarkTheme) SixpackTheme.colors.gray0 else SixpackTheme.colors.gray800,
        )
    }
}

@Preview
@Composable
private fun DoRunTitleTopBarPreview() {
    SixpackTheme {
        Surface(color = SixpackTheme.colors.gray0) {
            Column {
                DoRunNavigationTopBar(
                    navigateToBack = {},
                    modifier = Modifier,
                    titleContent = {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = SixpackTheme.typography.h2Bold,
                            color = SixpackTheme.colors.gray900,
                        )
                    },
                )
                DoRunNavigationTopBar(
                    navigateToBack = {},
                    modifier = Modifier,
                )
            }
        }
    }
}
