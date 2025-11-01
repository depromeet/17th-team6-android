package com.dpm.sixpack.presentation.routes.running.map.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.routes.freind.contract.FriendUiState
import com.dpm.sixpack.presentation.routes.running.RunningRouteUiState
import com.dpm.sixpack.presentation.routes.running.map.contract.state.PathColorState
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapUiState(
    val isFollowingModeEnabled: Boolean = true,
    val mapViewState: MapViewState = MapViewState.Loading,
) : RunningRouteUiState

sealed interface MapViewState : Parcelable {
    @Parcelize
    data object Loading : MapViewState

    sealed interface HasPathColorState : MapViewState {
        val pathColorState: PathColorState
    }

    @Parcelize
    data class Friend(
        val friendState: FriendUiState = FriendUiState(),
    ) : MapViewState

    @Parcelize
    data class Running(
        override val pathColorState: PathColorState = PathColorState(),
    ) : HasPathColorState

    @Parcelize
    data class Finishing(
        override val pathColorState: PathColorState = PathColorState(),
        val latLngBounds: LatLngBounds,
    ) : HasPathColorState
}
