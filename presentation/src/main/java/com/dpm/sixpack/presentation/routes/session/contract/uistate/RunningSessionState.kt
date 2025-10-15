package com.dpm.sixpack.presentation.routes.session.contract.uistate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface RunningSessionState : Parcelable {
    /**
     * [러닝 지도 뷰](https://www.figma.com/design/2gOt25L1n1LkFz15uvRapV/%EB%94%94%ED%94%84%EB%A7%8C-17%EA%B8%B0-6%ED%8C%80?node-id=1548-5420&m=dev)
     * 러닝 시작 버튼만 떠있는 상태.
     */
    data object Initial : RunningSessionState

    sealed interface HasRecord : RunningSessionState {
        val recordUiState: RecordUiState
    }

    //region MainRunning

    data class Ready(
        val countdown: Int = INITIAL_COUNTDOWN,
    ) : RunningSessionState {
        val onlyText get() = countdown > NUMBER_COUNTDOWN

        fun withNewCountdown(newCountdown: Int) = this.copy(countdown = newCountdown)
    }

    @Parcelize
    data class Running(
        val mapUiState: MapUiState = MapUiState(),
        override val recordUiState: RecordUiState = RecordUiState(),
    ) : HasRecord {
        fun withNewRecordUiState(newRecordUiState: RecordUiState) = this.copy(recordUiState = newRecordUiState)

        fun withNewMapUiState(newMapUiState: MapUiState) = this.copy(mapUiState = newMapUiState)
    }

    @Parcelize
    data class Pause(
        val mapUiState: MapUiState = MapUiState(),
        val showStopSessionConfirmDialog: Boolean = false,
        override val recordUiState: RecordUiState = RecordUiState(),
    ) : HasRecord {
        fun withNewShowStopConfirmDialog(newShowStopConfirmDialog: Boolean) =
            this.copy(showStopSessionConfirmDialog = newShowStopConfirmDialog)
    }

    //endregion

    companion object {
        const val INITIAL_COUNTDOWN = 5
        const val NUMBER_COUNTDOWN = 3
    }
}
