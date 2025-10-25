package com.dpm.sixpack.presentation.routes.signup.ui.component.terms

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
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

@Composable
fun CheckToggle(
    modifier: Modifier = Modifier,
    onClick: (Boolean) -> Unit = {},
    isChecked: Boolean = false,
) {
    val iconRes =
        if (isChecked) {
            R.drawable.ic_check_fill
        } else {
            R.drawable.ic_check
        }

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .size(32.dp)
                .noRippleClickable(onClick = { onClick(!isChecked) }),
    ) {
        Image(
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = null,
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
