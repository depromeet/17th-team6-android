package com.dpm.sixpack.presentation.routes.session.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.naver.maps.geometry.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class RunningSessionUiState(
    val sessionState: RunningSessionState = RunningSessionState.Initial,
    val isFollowingModeEnabled: Boolean = true,
) : UiState,
    Parcelable

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
    data object Initial : RunningSessionState

    sealed interface HasRecord : RunningSessionState {
        val recordUiState: RecordUiState
    }

    sealed interface ReadyState :
        RunningSessionState,
        Parcelable {
        val countdown: Int
        val onlyText get() = countdown > NUMBER_COUNTDOWN
    }

    sealed interface RunningState :
        RunningSessionState,
        HasRecord

    sealed interface PausedState :
        RunningSessionState,
        HasRecord

    //region WarmUp

    @Parcelize
    sealed interface WarmUp : Parcelable {
        data class Ready(
            override val countdown: Int = INITIAL_COUNTDOWN,
        ) : ReadyState,
            WarmUp

        data class Running(
            val showSkipConfirmUi: Boolean = false,
            val remainingDistance: String = "",
            override val recordUiState: RecordUiState = RecordUiState(),
        ) : RunningState,
            WarmUp

        data class Pause(
            val mapUiState: MapUiState = MapUiState(),
            val showFinishSessionConfirmUi: Boolean = false,
            override val recordUiState: RecordUiState = RecordUiState(),
        ) : PausedState,
            WarmUp
    }

    //endregion

    //region MainRunning

    @Parcelize
    sealed interface Main : Parcelable {
        data class Ready(
            override val countdown: Int = INITIAL_COUNTDOWN,
        ) : ReadyState,
            Main

        data class Running(
            val goalDistance: String = "",
            val mapUiState: MapUiState = MapUiState(),
            override val recordUiState: RecordUiState = RecordUiState(),
        ) : RunningState,
            Main

        data class Pause(
            val goalDistance: String = "",
            val mapUiState: MapUiState = MapUiState(),
            val showFinishSessionConfirmUi: Boolean = false,
            override val recordUiState: RecordUiState = RecordUiState(),
            // 다이얼로그에서 사용.
            val remainingDistance: String = "",
        ) : PausedState,
            Main
    }

    //endregion

    //region CoolDown

    @Parcelize
    sealed interface CoolDown : Parcelable {
        data class Ready(
            override val countdown: Int = INITIAL_COUNTDOWN,
            val mapUiState: MapUiState = MapUiState(),
        ) : ReadyState,
            CoolDown

        data class Running(
            override val recordUiState: RecordUiState = RecordUiState(),
        ) : RunningState,
            CoolDown

        data class Pause(
            val mapUiState: MapUiState = MapUiState(),
            val showFinishSessionConfirmUi: Boolean = false,
            override val recordUiState: RecordUiState = RecordUiState(),
        ) : PausedState,
            CoolDown
    }

    //endregion

    companion object {
        const val INITIAL_COUNTDOWN = 5
        const val NUMBER_COUNTDOWN = 3
    }
}

@Parcelize
data class RecordUiState(
    val currentDistance: String = "",
    // 00:32:10
    val currentDuration: String = "",
    // 5'30"
    val avgPace: String = "",
    // 180
    val cadence: String = "",
) : Parcelable

@Parcelize
data class MapUiState(
    val paceColors: List<List<ULong>> = listOf(),
    val path: List<List<LatLng>> = listOf(),
) : Parcelable
