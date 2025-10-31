package com.dpm.sixpack.presentation.routes.running.map.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.routes.freind.contract.FriendUiState
import com.dpm.sixpack.presentation.routes.running.RunningRouteUiState
import com.dpm.sixpack.presentation.routes.running.map.contract.state.PathColorState
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapUiState(
    val isFollowingModeEnabled: Boolean = true,
    val mapViewState: MapViewState = MapViewState.Friend(),
) : RunningRouteUiState

sealed interface MapViewState : Parcelable {
    @Parcelize
    data object Loading : MapViewState

    @Parcelize
    data class Friend(
        val friendState: FriendUiState = FriendUiState(),
    ) : MapViewState

    @Parcelize
    data class Running(
        val pathColorState: PathColorState = PathColorState(),
    ) : MapViewState
}
