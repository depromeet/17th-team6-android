package com.dpm.sixpack.presentation.common.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixPackDimen
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 푸시 알림 설정 아이템 컴포넌트
 *
 * @param title 알림 제목
 * @param description 알림 설명
 * @param checked 토글 상태
 * @param onCheckedChange 토글 상태 변경 이벤트
 * @param modifier Modifier
 */
@Composable
fun NotificationSettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = SixPackDimen.defaultSideMargin, vertical = 8.dp)
                .padding(start = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = SixpackTheme.typography.b2Medium,
                color = SixpackTheme.colors.gray900,
            )

            Text(
                text = description,
                style = SixpackTheme.typography.c1Regular,
                color = SixpackTheme.colors.gray500,
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = SixpackTheme.colors.gray0,
                    checkedTrackColor = SixpackTheme.colors.blue600,
                    uncheckedThumbColor = SixpackTheme.colors.gray0,
                    uncheckedTrackColor = SixpackTheme.colors.gray300,
                    uncheckedBorderColor = SixpackTheme.colors.gray300,
                ),
        )
    }
}

@Preview
@Composable
private fun NotificationSettingItemCheckedPreview() {
    DoRunPreviewWrapper {
        NotificationSettingItem(
            title = "마케팅 푸시",
            description = "마케팅 정보 수신 동의 2025.10.27",
            checked = true,
            onCheckedChange = {},
        )
    }
}

@Preview
@Composable
private fun NotificationSettingItemUncheckedPreview() {
    DoRunPreviewWrapper {
        NotificationSettingItem(
            title = "알림 받기",
            description = "다양한 알림을 실시간으로 받아요.",
            checked = false,
            onCheckedChange = {},
        )
    }
}
