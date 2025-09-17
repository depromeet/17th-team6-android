package com.dpm.sixpack.presentation.routes.session.contract

import android.os.Parcelable
import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.common.util.base.UiState
import com.naver.maps.geometry.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class RunningSessionUiState(
    val state: RunningSessionState = RunningSessionState.Initial,
    val isFollowingModeEnabled: Boolean = true,
) : UiState,
    Parcelable

/**
 * 작성중..
 * - 거리는 1km 를 기점으로 m 에서km로 단위가 변경. ex. 5.0km, 200m
 */
@Parcelize
sealed class RunningSessionState : Parcelable {
    /**
     * [러닝 지도 뷰](https://www.figma.com/design/2gOt25L1n1LkFz15uvRapV/%EB%94%94%ED%94%84%EB%A7%8C-17%EA%B8%B0-6%ED%8C%80?node-id=1548-5420&m=dev)
     * 러닝 시작 버튼만 떠있는 상태.
     */
    data object Initial : RunningSessionState()

    //region WarmUp

    data class WarmUpReady(
        val countdown: Int = INITIAL_COUNTDOWN,
        val onlyText: Boolean = countdown > NUMBER_COUNTDOWN,
    ) : RunningSessionState()

    data class WarmUp(
        // 00:00:00
        val time: String = "",
        val showSkipConfirmUi: Boolean = false,
        val remainingDistance: String = "",
    ) : RunningSessionState()

    //endregion

    //region MainRunning

    data class MainRunningReady(
        val countdown: Int = INITIAL_COUNTDOWN,
        val onlyText: Boolean = countdown > NUMBER_COUNTDOWN,
    ) : RunningSessionState()

    data class MainRunning(
        val goalDistance: String = "",
        val mapUiState: MapUiState = MapUiState(),
        val recordUiState: RecordUiState = RecordUiState(),
    ) : RunningSessionState()

    data class MainRunningPause(
        val goalDistance: String = "",
        val mapUiState: MapUiState = MapUiState(),
        val recordUiState: RecordUiState = RecordUiState(),
        val showFinishSessionConfirmUi: Boolean = false,
        // 다이얼로그에서 사용.
        val remainingDistance: String = "",
    ) : RunningSessionState()

    //endregion

    //region CoolDown

    data class CoolDownReady(
        val countdown: Int = INITIAL_COUNTDOWN,
        val onlyText: Boolean = countdown > NUMBER_COUNTDOWN,
        val mapUiState: MapUiState = MapUiState(),
    ) : RunningSessionState()

    data class CoolDown(
        // 00:00:00
        val time: String = "",
        val mapUiState: MapUiState = MapUiState(),
    ) : RunningSessionState()

    data class CoolDownPause(
        // 00:00:00
        val time: String = "",
        val mapUiState: MapUiState = MapUiState(),
        val showFinishSessionConfirmUi: Boolean = false,
    ) : RunningSessionState()

    //endregion

    companion object {
        const val INITIAL_COUNTDOWN = 5
        const val NUMBER_COUNTDOWN = 3
    }
}

// FIXME : string 리소스 연결 필요
enum class ReadyPhase(
    @StringRes val title: Int,
    @StringRes val description: Int,
) {
    WarmUp(title = 0, description = 0),
    Main(title = 0, description = 0),
    CoolDown(title = 0, description = 0),
}

@Parcelize
data class RecordUiState(
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
