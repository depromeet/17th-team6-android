package com.dpm.sixpack.presentation.routes.mypage.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun MyPageTabText(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style =
            if (isSelected) {
                SixpackTheme.typography.b1Bold
            } else {
                SixpackTheme.typography.b1Regular
            },
        color =
            if (isSelected) {
                SixpackTheme.colors.gray900
            } else {
                SixpackTheme.colors.gray500
            },
        modifier = modifier,
    )
}

@Preview
@Composable
private fun MyPageTabTextSelectedPreview() {
    DoRunPreviewWrapper {
        MyPageTabText(
            text = "인증",
            isSelected = true,
        )
    }
}

@Preview
@Composable
private fun MyPageTabTextUnselectedPreview() {
    DoRunPreviewWrapper {
        MyPageTabText(
            text = "기록",
            isSelected = false,
        )
    }
}
