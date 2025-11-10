package com.dpm.sixpack.presentation.routes.running.map.contract

import android.graphics.Bitmap
import com.dpm.sixpack.presentation.common.model.FriendItem
import com.dpm.sixpack.presentation.routes.running.RunningRouteIntent
import com.dpm.sixpack.presentation.routes.running.session.contract.state.PathState
import com.naver.maps.geometry.LatLng

sealed interface MapIntent : RunningRouteIntent {
    data object ToggleFollowingMode : MapIntent

    data object FollowingModeOff : MapIntent

    data class UpdateUserLocation(
        val latLng: LatLng,
    ) : MapIntent

    data object SessionStartFailed : MapIntent

    data class UpdateRunningMapPath(
        val pathState: PathState,
    ) : MapIntent

    data object SessionStartClick : MapIntent

    data object ReadyToFinish : MapIntent

    data class SessionFinish(
        val mapImage: Bitmap,
    ) : MapIntent

    // region Permission

    /** 모든 권한 (전경 + 백그라운드)이 최종 승인됨 */
    data object AllPermissionsGranted : MapIntent

    /** 권한 중 하나라도 최종 거부됨 */
    data object PermissionsRejected : MapIntent

    data object RequestBackgroundPermissionDialog : MapIntent

    data object DismissBackgroundPermissionDialog : MapIntent
    // endregion

    sealed interface FriendSheetIntent : MapIntent {
        data class ClickFriendItem(
            val friend: FriendItem,
        ) : FriendSheetIntent

        data class AwakeFriend(
            val userId: Long,
        ) : FriendSheetIntent

        data object FriendIconClick : FriendSheetIntent
    }
}
