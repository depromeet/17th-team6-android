package com.dpm.sixpack.presentation.routes.running.session.contract

import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.routes.running.session.contract.state.PathState
import com.dpm.sixpack.presentation.routes.running.session.contract.state.RecordState
import com.naver.maps.geometry.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface RunningSessionUiState : UiState {
    /**
     * [러닝 지도 뷰](https://www.figma.com/design/2gOt25L1n1LkFz15uvRapV/%EB%94%94%ED%94%84%EB%A7%8C-17%EA%B8%B0-6%ED%8C%80?node-id=1548-5420&m=dev)
     * 러닝 시작 버튼만 떠있는 상태.
     */
    data object Initial : RunningSessionUiState

    sealed interface HasRecord : RunningSessionUiState {
        val recordState: RecordState
    }

    //region MainRunning

    data class Ready(
        val countdown: Int = INITIAL_COUNTDOWN,
    ) : RunningSessionUiState {
        val onlyText get() = countdown > NUMBER_COUNTDOWN

        fun withNewCountdown(newCountdown: Int) = this.copy(countdown = newCountdown)
    }

    @Parcelize
    data class Running(
        override val recordState: RecordState = RecordState(),
        val pathState: PathState = PathState(),
    ) : HasRecord {
        fun withNewRecordState(newRecordState: RecordState) = this.copy(recordState = newRecordState)
    }

    @Parcelize
    data class Pause(
        override val recordState: RecordState = RecordState(),
        val pathState: PathState = PathState(),
        val showStopSessionConfirmDialog: Boolean = false,
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
