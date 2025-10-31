package com.dpm.sixpack.presentation.routes.running.map.contract

import android.graphics.Bitmap
import android.net.Uri
import com.dpm.sixpack.presentation.routes.running.RunningRouteIntent
import com.dpm.sixpack.presentation.routes.running.session.contract.state.PathState
import com.naver.maps.geometry.LatLng

sealed interface MapIntent : RunningRouteIntent {
    data object ToggleFollowingMode : MapIntent

    data object FollowingModeOff : MapIntent

    data class UpdateUserLocation(
        val latLng: LatLng,
    ) : MapIntent

    data class UpdatePermission(
        val isGranted: Boolean,
    ) : MapIntent

    data class UpdateRunningMapPath(
        val pathState: PathState,
    ) : MapIntent

    data object SessionStartClick : MapIntent

    data object ReadyToFinish : MapIntent

    data class SessionFinish(
        val mapImage: Bitmap,
    ) : MapIntent
}
