package com.dpm.sixpack.presentation.routes.signup.ui.component.terms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
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
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.routes.signup.ui.component.terms.model.TermType
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun TermRow(
    term: TermType,
    isChecked: Boolean,
    onClickToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onTermDetailClick: (String) -> Unit = {},
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
            text = stringResource(term.title),
            style = SixpackTheme.typography.b1Regular,
            color = SixpackTheme.colors.gray700,
        )

        Spacer(modifier = Modifier.weight(1f))

        TermDetailButton(
            onClick = { onTermDetailClick(term.url) },
        )
    }
}

@Composable
fun TermDetailButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .size(32.dp)
                .noRippleClickable(onClick = onClick),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            modifier =
                Modifier
                    .size(24.dp)
                    .padding(8.dp),
            tint = SixpackTheme.colors.gray200,
        )
    }
}

@Preview
@Composable
private fun TermRowPreview() {
    DoRunPreviewWrapper {
        TermRow(
            term = TermType.MARKETING,
            isChecked = false,
            onClickToggle = {},
        )
    }
}
