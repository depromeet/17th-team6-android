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
                label = stringResource(R.string.record_average_pace),
                recordValue = formatPaceToString(recordState.avgPace),
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
                recordValue = "${recordState.cadence} spm",
            )
        }
    }
}

@Composable
internal fun PrePostRunningRecordGrid(
    recordState: RecordState,
    modifier: Modifier = Modifier,
) {
    // TODO SK: Change to string resources
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
    ) {
        // 왼쪽 열
        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            RecordItem(
                label = stringResource(R.string.record_running_duration),
                recordValue = formatSecondsToTime(recordState.currentDuration),
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
                avgPace = 440,
                cadence = 154,
            ),
        )
    }
}

@Preview
@Composable
private fun PreviewPrePostRecordGrid() {
    Box(
        modifier =
            Modifier
                .background(color = Color.White)
                .padding(10.dp),
    ) {
        PrePostRunningRecordGrid(
            RecordState(
                currentDistance = 15400,
                currentDuration = 1530,
                avgPace = 440,
                cadence = 154,
            ),
        )
    }
}
