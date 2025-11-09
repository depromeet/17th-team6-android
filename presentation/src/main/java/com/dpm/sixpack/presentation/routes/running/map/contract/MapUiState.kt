package com.dpm.sixpack.presentation.routes.running.map.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.model.FriendUiItem
import com.dpm.sixpack.presentation.routes.running.RunningRouteUiState
import com.naver.maps.geometry.LatLngBounds
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapUiState(
    val isFollowingModeEnabled: Boolean = true,
    val isStartButtonEnabled: Boolean = true,
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
        val selectedFriend: FriendUiItem? = null,
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
