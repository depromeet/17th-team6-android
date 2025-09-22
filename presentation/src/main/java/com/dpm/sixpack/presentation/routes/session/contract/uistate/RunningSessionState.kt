package com.dpm.sixpack.presentation.routes.session.contract.uistate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 작성중..
 * - 거리는 1km 를 기점으로 m 에서km로 단위가 변경. ex. 5.0km, 200m
 */
@Parcelize
sealed interface RunningSessionState : Parcelable {
    /**
     * [러닝 지도 뷰](https://www.figma.com/design/2gOt25L1n1LkFz15uvRapV/%EB%94%94%ED%94%84%EB%A7%8C-17%EA%B8%B0-6%ED%8C%80?node-id=1548-5420&m=dev)
     * 러닝 시작 버튼만 떠있는 상태.
     */
    data class Initial(
        val goal: RunningGoalUiState = RunningGoalUiState(),
    ) : RunningSessionState

    // 러닝 패널에 표시될 기록이 있는 상태
    sealed interface HasRecord : RunningSessionState {
        val recordUiState: RecordUiState
    }

    sealed interface ReadyState :
        RunningSessionState,
        Parcelable {
        val countdown: Int
        val onlyText get() = countdown > NUMBER_COUNTDOWN

        fun withNewCountdown(newCountdown: Int): ReadyState
    }

    sealed interface RunningState :
        RunningSessionState,
        HasRecord {
        fun withNewRecordUiState(newRecordUiState: RecordUiState): RunningState
    }

    sealed interface PausedState :
        RunningSessionState,
        HasRecord {
        val showStopSessionConfirmDialog: Boolean

        fun withNewShowStopConfirmDialog(newShowStopConfirmDialog: Boolean): PausedState
    }

    //region WarmUp

    @Parcelize
    sealed interface WarmUp : Parcelable {
        data class Ready(
            override val countdown: Int = INITIAL_COUNTDOWN,
        ) : ReadyState,
            WarmUp {
            override fun withNewCountdown(newCountdown: Int): ReadyState = this.copy(countdown = newCountdown)
        }

        data class Running(
            override val recordUiState: RecordUiState = RecordUiState(),
        ) : RunningState,
            WarmUp {
            override fun withNewRecordUiState(newRecordUiState: RecordUiState): RunningState =
                this.copy(recordUiState = newRecordUiState)
        }

        data class Pause(
            val showSkipConfirmDialog: Boolean = false,
            override val showStopSessionConfirmDialog: Boolean = false,
            override val recordUiState: RecordUiState = RecordUiState(),
        ) : PausedState,
            WarmUp {
            override fun withNewShowStopConfirmDialog(newShowStopConfirmDialog: Boolean): PausedState =
                this.copy(showStopSessionConfirmDialog = newShowStopConfirmDialog)
        }
    }

    //endregion

    //region MainRunning

    @Parcelize
    sealed interface Main : Parcelable {
        data class Ready(
            override val countdown: Int = INITIAL_COUNTDOWN,
        ) : ReadyState,
            Main {
            override fun withNewCountdown(newCountdown: Int): ReadyState = this.copy(countdown = newCountdown)
        }

        data class Running(
            val goalDistance: String = "",
            val mapUiState: MapUiState = MapUiState(),
            override val recordUiState: RecordUiState = RecordUiState(),
        ) : RunningState,
            Main {
            override fun withNewRecordUiState(newRecordUiState: RecordUiState): RunningState =
                this.copy(recordUiState = newRecordUiState)

            fun withNewMapUiState(newMapUiState: MapUiState): RunningState = this.copy(mapUiState = newMapUiState)
        }

        data class Pause(
            val remainDistance: String = "",
            val goalDistance: String = "",
            val mapUiState: MapUiState = MapUiState(),
            override val showStopSessionConfirmDialog: Boolean = false,
            override val recordUiState: RecordUiState = RecordUiState(),
            // 다이얼로그에서 사용.
            val remainingDistance: String = "",
        ) : PausedState,
            Main {
            override fun withNewShowStopConfirmDialog(newShowStopConfirmDialog: Boolean): PausedState =
                this.copy(showStopSessionConfirmDialog = newShowStopConfirmDialog)
        }
    }

    //endregion

    //region CoolDown

    @Parcelize
    sealed interface CoolDown : Parcelable {
        data class Ready(
            val mapUiState: MapUiState = MapUiState(),
            override val countdown: Int = INITIAL_COUNTDOWN,
        ) : ReadyState,
            CoolDown {
            override fun withNewCountdown(newCountdown: Int): ReadyState = this.copy(countdown = newCountdown)
        }

        data class Running(
            val mapUiState: MapUiState = MapUiState(),
            override val recordUiState: RecordUiState = RecordUiState(),
        ) : RunningState,
            CoolDown {
            override fun withNewRecordUiState(newRecordUiState: RecordUiState): RunningState =
                this.copy(recordUiState = newRecordUiState)
        }

        data class Pause(
            val mapUiState: MapUiState = MapUiState(),
            override val showStopSessionConfirmDialog: Boolean = false,
            override val recordUiState: RecordUiState = RecordUiState(),
        ) : PausedState,
            CoolDown {
            override fun withNewShowStopConfirmDialog(newShowStopConfirmDialog: Boolean): PausedState =
                this.copy(showStopSessionConfirmDialog = newShowStopConfirmDialog)
        }
    }

    //endregion

    companion object {
        const val INITIAL_COUNTDOWN = 5
        const val NUMBER_COUNTDOWN = 3
    }
}
