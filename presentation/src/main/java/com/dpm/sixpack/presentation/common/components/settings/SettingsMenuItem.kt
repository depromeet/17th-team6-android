package com.dpm.sixpack.presentation.common.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixPackDimen
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 설정 메뉴 아이템 컴포넌트
 *
 * @param title 메뉴 제목
 * @param modifier Modifier
 * @param endContent 오른쪽에 표시될 컨텐츠 (버전 정보 등)
 * @param showArrow 오른쪽 화살표 아이콘 표시 여부
 * @param onClick 클릭 이벤트
 */
@Composable
fun SettingsMenuItem(
    title: String,
    modifier: Modifier = Modifier,
    endContent: (@Composable () -> Unit)? = null,
    showArrow: Boolean = true,
    onClick: () -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(44.dp)
                .clickable(onClick = onClick)
                .padding(horizontal = SixPackDimen.defaultSideMargin, vertical = 10.dp)
                .padding(start = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray900,
        )

        if (endContent != null) {
            endContent()
        } else if (showArrow) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "Navigate to $title",
                modifier = Modifier.size(24.dp),
                tint = SixpackTheme.colors.gray400,
            )
        }
    }
}

@Preview
@Composable
private fun SettingsMenuItemPreview() {
    DoRunPreviewWrapper {
        SettingsMenuItem(
            title = "프로필 수정",
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun SettingsMenuItemWithEndContentPreview() {
    DoRunPreviewWrapper {
        SettingsMenuItem(
            title = "버전 정보",
            endContent = {
                Text(
                    text = "3.13.0",
                    style = SixpackTheme.typography.b2Regular,
                    color = SixpackTheme.colors.gray500,
                )
            },
            showArrow = false,
            onClick = {},
        )
    }
}
