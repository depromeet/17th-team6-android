package com.dpm.sixpack.presentation.routes.running.session.component.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.record.RecordItem
import com.dpm.sixpack.presentation.common.util.formatDistanceToKm
import com.dpm.sixpack.presentation.common.util.formatPaceToString
import com.dpm.sixpack.presentation.common.util.formatSecondsToTime
import com.dpm.sixpack.presentation.routes.running.session.contract.state.RecordState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun MainRunningRecordGrid(
    recordState: RecordState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        // 왼쪽 열
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
        ) {
            RecordItem(
                label = stringResource(R.string.record_current_distance),
                recordValue = formatDistanceToKm(recordState.currentDistance),
                emphasize = true,
                textColor = SixpackTheme.colors.blue600,
            )
            Spacer(modifier = Modifier.height(20.dp))
            RecordItem(
                label = stringResource(R.string.record_pace),
                recordValue = if (recordState.pace <= 0) "-'--\"" else formatPaceToString(recordState.pace),
            )
        }
        // 오른쪽 열
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
        ) {
            RecordItem(
                label = stringResource(R.string.record_running_duration),
                recordValue = formatSecondsToTime(recordState.currentDuration),
                emphasize = true,
            )
            Spacer(modifier = Modifier.height(20.dp))
            RecordItem(
                label = stringResource(R.string.record_cadence),
                recordValue = if (recordState.cadence <= 0) "- spm" else "${recordState.cadence} spm",
            )
        }
    }
}

@Preview
@Composable
private fun PreviewMainRecordGrid() {
    Box(
        modifier =
            Modifier
                .background(color = Color.White)
                .padding(10.dp),
    ) {
        MainRunningRecordGrid(
            RecordState(
                currentDistance = 15400,
                currentDuration = 1530,
                pace = 0,
                cadence = 0,
            ),
        )
    }
}
