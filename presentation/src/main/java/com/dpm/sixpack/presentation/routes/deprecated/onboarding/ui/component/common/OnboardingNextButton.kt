package com.dpm.sixpack.presentation.routes.deprecated.onboarding.ui.component.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun OnboardingNextButton(
    @StringRes text: Int = R.string.button_default_next,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val buttonColor =
        if (enabled) {
            SixpackTheme.colors.gray0
        } else {
            SixpackTheme.colors.gray400
        }

    val contatinerColor =
        if (enabled) {
            SixpackTheme.colors.blue600
        } else {
            SixpackTheme.colors.gray100
        }

    DoRunDefaultButton(
        text = stringResource(text),
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        textColor = buttonColor,
        containerColor = contatinerColor,
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
    )
}

@Preview
@Composable
private fun OnboardingNextButtonPreview() {
    DoRunPreviewWrapper {
        OnboardingNextButton(
            onClick = { },
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
