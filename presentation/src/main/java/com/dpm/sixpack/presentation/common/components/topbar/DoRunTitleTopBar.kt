package com.dpm.sixpack.presentation.common.components.topbar

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
fun DoRunTitleTopBar(
    navigateToBack : () -> Unit,
    @StringRes title : Int,
    modifier: Modifier = Modifier
) {
    DoRunTopBarSlot(
        modifier = modifier.statusBarsPadding(),
        leadingContent = {
            NavigateBackButton(
                onClick = navigateToBack
            )
        }
        ,
        content = {
            Text(
                text = stringResource(id = title),
                style = SixpackTheme.typography.t2Bold,
            )
        }
    )
}

@Composable
fun NavigateBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(24.dp)
            .noRippleClickable(onClick = onClick),
    ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_left),
                contentDescription = null,
                tint = SixpackTheme.colors.gray800,
            )
    }
}

@Preview
@Composable
private fun DoRunTitleTopBarPreview() {
    SixpackTheme {
        Surface(color = SixpackTheme.colors.gray0) {
            DoRunTitleTopBar(
                navigateToBack = {},
                title = R.string.app_name,
                modifier = Modifier
            )
        }
    }
}
