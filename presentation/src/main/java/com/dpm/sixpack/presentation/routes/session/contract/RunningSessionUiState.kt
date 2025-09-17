package com.dpm.sixpack.presentation.routes.session.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.util.base.UiState
import com.naver.maps.geometry.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class RunningSessionUiState(
    val state: RunningSessionState = RunningSessionState.Initial,
    val isFollowingModeEnabled: Boolean = true,
) : UiState,
    Parcelable

@Parcelize
sealed class RunningSessionState : Parcelable {
    // 러닝 시작 버튼만 떠있는 상태.
    data object Initial : RunningSessionState()

    data class MainReady(
        val countdown: Int = INITIAL_COUNTDOWN,
    ) : RunningSessionState()

    data class MainRunning(
        val mapUiState: MapUiState = MapUiState(),
        val recordUiState: RecordUiState = RecordUiState(),
    ) : RunningSessionState()

    data class MainPause(
        val mapUiState: MapUiState = MapUiState(),
        val recordUiState: RecordUiState = RecordUiState(),
        val showExitConfirmUi: Boolean = false,
    ) : RunningSessionState()

    data class CoolDownReady(
        val mapUiState: MapUiState = MapUiState(),
        val countdown: Int = INITIAL_COUNTDOWN,
    ) : RunningSessionState()

    data object Finished : RunningSessionState()

    companion object {
        const val INITIAL_COUNTDOWN = 3
    }
}

@Parcelize
data class RecordUiState(
    // 1.50km
    val currentDistance: String = "",
    // 00:32:10
    val remainingDuration: String = "",
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
