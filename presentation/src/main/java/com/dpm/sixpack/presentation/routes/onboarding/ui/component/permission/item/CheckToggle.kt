package com.dpm.sixpack.presentation.routes.onboarding.ui.component.permission.item

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun CheckToggle(
    modifier: Modifier = Modifier,
    onClick: (Boolean) -> Unit = {},
    isChecked: Boolean = false,
) {
    val color =
        if (isChecked) {
            SixpackTheme.colors.blue600
        } else {
            SixpackTheme.colors.gray200
        }

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .size(32.dp)
                .noRippleClickable(onClick = { onClick(!isChecked) }),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_check),
            contentDescription = null,
            tint = color,
        )
    }
}

@Preview
@Composable
private fun CheckTogglePreview() {
    DoRunPreviewWrapper {
        CheckToggle()
    }
}
