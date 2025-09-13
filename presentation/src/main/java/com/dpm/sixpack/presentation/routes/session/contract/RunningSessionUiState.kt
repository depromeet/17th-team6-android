package com.dpm.sixpack.presentation.routes.session.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.util.base.UiState
import com.naver.maps.geometry.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class RunningSessionUiState :
    UiState,
    Parcelable {
    data object Initial : RunningSessionUiState() // 러닝 시작 버튼만 떠있는 상태.

    data class MainReady(
        val countdown: Int = INITIAL_COUNTDOWN,
    ) : RunningSessionUiState()

    data class MainRunning(
        val mapUiState: MapUiState = MapUiState(),
        val recordUiState: RecordUiState = RecordUiState(),
    ) : RunningSessionUiState()

    data class MainPause(
        val mapUiState: MapUiState = MapUiState(),
        val recordUiState: RecordUiState = RecordUiState(),
        val showExitConfirmUi: Boolean = false,
    ) : RunningSessionUiState()

    data class CoolDownReady(
        val mapUiState: MapUiState = MapUiState(),
        val countdown: Int = INITIAL_COUNTDOWN,
    ) : RunningSessionUiState()

    data object Finished : RunningSessionUiState()

    companion object {
        const val INITIAL_COUNTDOWN = 3
    }
}

@Parcelize
data class RecordUiState(
    val currentDistance: String = "", // 1.50km
    val remainingDuration: String = "", // 00:32:10
    val avgPace: String = "", // 5'30"
    val cadence: String = "", // 180
) : Parcelable

@Parcelize
data class MapUiState(
    val paceColors: List<List<ULong>> = listOf(),
    val path: List<List<LatLng>> = listOf(),
    val isFollowingModeEnabled: Boolean = true,
) : Parcelable
