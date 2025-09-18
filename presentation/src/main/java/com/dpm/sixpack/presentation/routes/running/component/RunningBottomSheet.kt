package com.dpm.sixpack.presentation.routes.running.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionUiState
import com.naver.maps.map.compose.ExperimentalNaverMapApi

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun RunningBottomSheet(
    uiState: RunningSessionUiState,
    sheetState: SheetState,
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
) {
    ModalBottomSheet(
        modifier = modifier.fillMaxHeight(0.3f),
        onDismissRequest = { },
        sheetState = sheetState,
        dragHandle = null,
    ) {
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
        }
    }
}

@Preview
@Composable
private fun PreviewRunningBottomSheet() {
    RunningBottomSheet(
        uiState = RunningSessionUiState(),
        sheetState =
            rememberStandardBottomSheetState(
                initialValue = SheetValue.PartiallyExpanded,
            ),
        onClose = {},
    )
}
