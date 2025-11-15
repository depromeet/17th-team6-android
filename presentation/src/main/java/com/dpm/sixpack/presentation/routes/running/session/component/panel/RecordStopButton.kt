package com.dpm.sixpack.presentation.routes.running.session.component.panel

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun RecordStopButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DoRunDefaultButton(
        modifier = modifier,
        text = stringResource(R.string.panel_record_terminate),
        onClick = onClick,
        textColor = SixpackTheme.colors.gray900,
        containerColor = SixpackTheme.colors.gray100,
    )
}

@Composable
internal fun RecordStopConfirmButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DoRunDefaultButton(
        modifier = modifier,
        text = stringResource(R.string.panel_record_terminate),
        onClick = onClick,
        textColor = SixpackTheme.colors.red,
        containerColor = SixpackTheme.colors.redLight,
    )
}

@Preview
@Composable
private fun RecordStopButtonPreview() {
    RecordStopButton(
        onClick = {},
    )
}
