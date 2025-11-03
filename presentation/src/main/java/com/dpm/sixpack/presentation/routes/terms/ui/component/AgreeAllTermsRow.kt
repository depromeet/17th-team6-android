package com.dpm.sixpack.presentation.routes.terms.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun AgreeAllTermsRow(
    modifier: Modifier = Modifier,
    isChecked: Boolean = false,
    onClickToggle: (Boolean) -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CheckToggle(
            onClick = onClickToggle,
            isChecked = isChecked,
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = stringResource(R.string.onboarding_permission_agree_all),
            style = SixpackTheme.typography.b1Bold,
        )
    }
}

@Preview
@Composable
private fun AgreeAllTermsRowPreview() {
    DoRunPreviewWrapper {
        AgreeAllTermsRow()
    }
}
