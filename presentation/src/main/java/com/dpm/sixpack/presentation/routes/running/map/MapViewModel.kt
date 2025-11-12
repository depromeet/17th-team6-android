package com.dpm.sixpack.presentation.routes.running.map

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.annotation.RequiresPermission
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.dpm.sixpack.domain.model.Friend
import com.dpm.sixpack.domain.usecase.friend.GetFriendRunningStatusUseCase
import com.dpm.sixpack.domain.usecase.friend.PostFriendNotificationUseCase
import com.dpm.sixpack.domain.usecase.running.FinishRunningSessionUseCase
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.model.FriendItem
import com.dpm.sixpack.presentation.common.model.toUiItem
import com.dpm.sixpack.presentation.routes.running.map.contract.MapIntent
import com.dpm.sixpack.presentation.routes.running.map.contract.MapSideEffect
import com.dpm.sixpack.presentation.routes.running.map.contract.MapUiState
import com.dpm.sixpack.presentation.routes.running.map.contract.MapViewState
import com.dpm.sixpack.presentation.routes.running.session.contract.state.PathState
import com.google.android.gms.location.FusedLocationProviderClient
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

/*
 * 지도에 관련된 로직만 처리하는 뷰모델
 * 1. 지도 위치 권한 관련
 * 2. 카메라 업데이트
 * 3. 위치 업데이트
 */

@HiltViewModel
class MapViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val finishRunningSessionUseCase: FinishRunningSessionUseCase,
    private val getFriendRunningStatusUseCase: GetFriendRunningStatusUseCase,
    private val postFriendNotificationUseCase: PostFriendNotificationUseCase,
) : BaseViewModel<MapUiState, MapIntent, MapSideEffect>() {
    override val initialState: MapUiState = MapUiState()

    override val container: Container<MapUiState, MapSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    private val refreshTrigger = MutableStateFlow(0)

    val friendPagingFlow: Flow<PagingData<FriendItem>> =
        refreshTrigger
            .flatMapLatest {
                // 'refreshTrigger'의 값이 바뀔 때마다
                //  flatMapLatest가 기존 Flow를 취소하고 getFriendRunningStatusUseCase()를 재호출합니다.
                getFriendRunningStatusUseCase()
            }.map { pagingData: PagingData<Friend> ->
                pagingData.map { friend: Friend ->
                    friend.toUiItem()
                }
            }.cachedIn(viewModelScope)

    init {
        intent {
            delay(1000L)
            reduce {
                state.copy(
                    mapViewState = MapViewState.Friend(),
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onIntent(intent: MapIntent) {
        when (intent) {
            is MapIntent.FriendSheetIntent -> onFriendSheetIntent(intent)
            MapIntent.SessionStartClick -> handleSessionStartButtonClick()
            MapIntent.SessionStartFailed -> handleSessionStartFailed()
            MapIntent.ReadyToFinish -> handleSessionFinishReady()
            MapIntent.ToggleFollowingMode -> handleToggleFollowingMode()
            MapIntent.FollowingModeOff -> handleToggleFollowingModeOff()
            is MapIntent.SessionFinish -> handleSessionFinish(intent.mapImage)
            is MapIntent.UpdateUserLocation -> handleUserLocationChange(intent.latLng)
            MapIntent.AllPermissionsGranted -> handleAllPermissionsGranted()
            MapIntent.PermissionsRejected -> handlePermissionsRejected()
            is MapIntent.RequestBackgroundPermissionDialog -> handleBackgroundPermissionDialog(true)
            is MapIntent.DismissBackgroundPermissionDialog -> handleBackgroundPermissionDialog(false)
            is MapIntent.UpdateRunningMapPath -> updateRunningMapPath(intent.pathState)
        }
    }

    fun onFriendSheetIntent(intent: MapIntent.FriendSheetIntent) {
        when (intent) {
            is MapIntent.FriendSheetIntent.AwakeFriend -> handleAwakeFriend(intent.userId)
            is MapIntent.FriendSheetIntent.ClickFriendItem -> handleClickUser(intent.friend)
            MapIntent.FriendSheetIntent.FriendIconClick ->
                intent {
                    postSideEffect(MapSideEffect.NavigateToFriendList)
                }
        }
    }

    private fun handleUserLocationChange(latLng: LatLng) {
        intent {
            if (state.isFollowingModeEnabled) {
                postSideEffect(MapSideEffect.SetCameraPosition(latLng))
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleToggleFollowingMode() =
        intent {
            val currentFollowMode = state.isFollowingModeEnabled
            if (!currentFollowMode) {
                loadLocationFromClient(
                    onSuccess = { latLng ->
                        viewModelScope.launch {
                            postSideEffect(MapSideEffect.SetCameraPosition(latLng))
                        }
                    },
                )
            }
            reduce {
                state.copy(
                    isFollowingModeEnabled = !currentFollowMode,
                )
            }
        }

    private fun handleToggleFollowingModeOff() {
        intent {
            reduce {
                state.copy(
                    isFollowingModeEnabled = false,
                )
            }
        }
    }

    // 👇 [수정] handleBackgroundPermissionGranted -> handleAllPermissionsGranted
    private fun handleAllPermissionsGranted() =
        intent {
            reduce {
                state.copy(
                    showRationaleDialog = false,
                    allPermissionsGranted = true, // 이름 변경
                )
            }
        }

    // 👇 [추가]
    private fun handlePermissionsRejected() =
        intent {
            reduce {
                state.copy(
                    allPermissionsGranted = false,
                )
            }
        }

    private fun handleSessionStartButtonClick() =
        intent {
            reduce {
                state.copy(
                    mapViewState = MapViewState.Running(),
                )
            }
            postSideEffect(MapSideEffect.SetBottomBarVisibility(false))
        }

    private fun handleSessionStartFailed() =
        intent {
            reduce {
                state.copy(
                    allPermissionsGranted = false, // 이름 변경
                )
            }
            postSideEffect(MapSideEffect.SetBottomBarVisibility(true))
            postSideEffect(MapSideEffect.ShowToast(R.string.running_permission_toast))
        }

    private fun handleBackgroundPermissionDialog(showDialog: Boolean) =
        intent {
            reduce {
                state.copy(
                    showRationaleDialog = showDialog,
                )
            }
        }

    // 세션 종료 준비
    private fun handleSessionFinishReady() =
        // 종료 스크린샷
        intent {
            val curState = state.mapViewState
            if (curState is MapViewState.Running) {
                val pathColorState = curState.pathColorState

                if (pathColorState.paths.isNotEmpty()) {
                    val bounds =
                        LatLngBounds
                            .Builder()
                            .include(pathColorState.paths.flatten())
                            .build()
                            .toSquareBounds()

                    reduce {
                        state.copy(
                            mapViewState = MapViewState.Finishing(pathColorState, bounds),
                        )
                    }
                } else {
                    // allRunningPaths 비어있음 = 러닝 안함
                    // TODO SK: 다이얼로그? ex) 러닝 기록이 없어요. 이대료 종료하면 저장되지 않아요.
                    reduce {
                        state.copy(
                            mapViewState = MapViewState.Friend(),
                        )
                    }
                }
            }

            postSideEffect(MapSideEffect.SetBottomBarVisibility(true))
        }

    private fun handleSessionFinish(mapImage: Bitmap?) =
        intent {
            if (mapImage != null) {
                finishRunningSessionUseCase(mapImage)
                    .onSuccess { id ->
                        Timber.d("session finish success, sessionId : $id")
                        postSideEffect(MapSideEffect.NavigateToReport(id))
                    }.onError {
                        Timber.d("session finish failed: ${it.message}")
                        postSideEffect(MapSideEffect.NavigateToReport(-1L))
                    }
            } else {
                postSideEffect(MapSideEffect.NavigateToReport(-1L))
            }
            reduce {
                state.copy(
                    mapViewState = MapViewState.Friend(),
                )
            }
        }

    private fun updateRunningMapPath(newPathState: PathState) =
        intent {
            val mapViewState = state.mapViewState
            if (mapViewState is MapViewState.Running) {
                reduce {
                    state.copy(
                        mapViewState =
                            MapViewState.Running(
                                pathColorState = mapViewState.pathColorState.updatedWith(newPathState),
                            ),
                    )
                }
            }
        }

    // TODO SK: 이 함수 호출부 모두 Client 직접 사용하는 방식 말고 유스케이스 거치는 방식으로 변경하기
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun loadLocationFromClient(onSuccess: (LatLng) -> Unit) {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    onSuccess(userLatLng)
                } else {
                    Timber.e("Last Location is Null")
                }
            }.addOnFailureListener {
                Timber.e("Load Location From Client failed: $it")
            }
    }

    /**
     * LatLngBounds를 포함하는 가장 작은 '정사각형' LatLngBounds로 변환하는 헬퍼 함수
     */
    private fun LatLngBounds.toSquareBounds(): LatLngBounds {
        // 원본 영역의 위도(높이)와 경도(너비) 차이를 계산
        val latDistance = this.northLatitude - this.southLatitude
        val lngDistance = this.eastLongitude - this.westLongitude

        // 더 큰 값을 기준으로 정사각형의 한 변의 길이를 정함
        val maxDistance = maxOf(latDistance, lngDistance)
        val halfMaxDistance = maxDistance / 2.0

        // 원본 영역의 중심점
        val center = this.center

        // 중심점에서 정사각형의 절반 크기만큼 떨어진 새 '정사각형' Bounds 생성
        return LatLngBounds(
            LatLng(center.latitude - halfMaxDistance, center.longitude - halfMaxDistance), // SW (남서)
            LatLng(center.latitude + halfMaxDistance, center.longitude + halfMaxDistance), // NE (북동)
        )
    }

    // region FriendSheet

    private fun handleAwakeFriend(userId: Long) =
        intent {
            postFriendNotificationUseCase(userId)
                .onSuccess { result ->
                    postSideEffect(MapSideEffect.ShowToast(R.string.friend_awake_toast, result))
                    refresh()
                }.onError { e ->
                    Timber.w("FriendSheetViewModel: Failed to post friend notification: ${e.message}")
                }
        }

    private fun handleClickUser(friend: FriendItem) =
        intent {
            val friendState = state.mapViewState as? MapViewState.Friend ?: return@intent
            reduce {
                state.copy(
                    isFollowingModeEnabled = false,
                    mapViewState =
                        friendState.copy(
                            selectedFriend = friend,
                        ),
                )
            }
            friend.lastRunInfo?.let {
                postSideEffect(
                    MapSideEffect.SetCameraPosition(
                        LatLng(
                            it.latitude,
                            it.longitude,
                        ),
                    ),
                )
            }
        }

    private fun refresh() =
        intent {
            refreshTrigger.value++
        }

    // endregion
}
