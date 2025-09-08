package com.dpm.sixpack.presentation.map

import MockLocationClient
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Debug
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dpm.sixpack.presentation.common.permission.PermissionHandler
import com.dpm.sixpack.presentation.map.component.MapConstants
import com.dpm.sixpack.presentation.map.component.MapConstants.MIN_LENGTH_PATH_ARRAY
import com.dpm.sixpack.presentation.map.contract.MapIntent
import com.dpm.sixpack.presentation.map.contract.MapSideEffect
import com.dpm.sixpack.presentation.map.contract.MapState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationSource
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapType
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PathOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapRoute(viewModel: MapViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // Location
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationSource = rememberFusedLocationSource()

    // State
    val uiState by viewModel.collectAsState()
    val cameraPositionState =
        rememberCameraPositionState {
            position = MapConstants.DEFAULT_CAMERA_POSITION
        }

    // DEBUG - MOCK LOCATION, REMOVE WHEN RELEASE
    val mockLocationClient =
        remember {
            MockLocationClient(fusedLocationClient, scope)
        }
    val isDebugMode = remember { Debug.isDebuggerConnected() }

    LaunchedEffect(uiState.runningMode) {
        if (isDebugMode && uiState.runningMode) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mockLocationClient.start(COORDS_1)
            }
        } else {
            mockLocationClient.stop()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mockLocationClient.stop()
        }
    }

    viewModel.collectSideEffect { mapSideEffect ->
        when (mapSideEffect) {
            is MapSideEffect.ShowToast -> {
                // TODO
            }

            is MapSideEffect.ScrollCameraPosition -> {
                cameraPositionState.move(CameraUpdate.scrollTo(mapSideEffect.latLng))
            }

            is MapSideEffect.ChangeCameraPosition -> {
                cameraPositionState.position = mapSideEffect.cameraPosition
            }

            is MapSideEffect.SetInitialLocation -> {
                if (!uiState.isInitialLocationSet && mapSideEffect.isGranted) {
                    fusedLocationClient.fetchLastLocation(
                        context,
                        onSuccess = { location ->
                            val userLatLng = LatLng(location)
                            viewModel.onIntent(MapIntent.SetInitialLocation(userLatLng))
                        },
                        onFailure = { },
                    )
                } else {
                    viewModel.onIntent(MapIntent.ChangeCameraPosition(MapConstants.DEFAULT_CAMERA_POSITION))
                }
            }
        }
    }

    PermissionHandler(
        context = context,
        lifecycleOwner = lifecycleOwner,
        permissionsToRequest = MapConstants.MAP_PERMISSIONS,
        onPermissionResult = { isGranted ->
            viewModel.onIntent(MapIntent.UpdateLocationPermission(isGranted))
        },
    )

    MapScreen(
        uiState = uiState,
        cameraPositionState = cameraPositionState,
        locationSource = locationSource,
        onLocationChange = { latLng ->
            viewModel.onIntent(MapIntent.UpdateUserLocation(latLng))
        },
        onFabClick = {
            val newRunningMode = !uiState.runningMode
            fusedLocationClient.fetchLastLocation(
                context,
                onSuccess = { location ->
                    val currentLatLng = LatLng(location)
                    viewModel.onIntent(
                        MapIntent.ChangeRunningMode(
                            newRunningMode,
                            currentLatLng,
                            cameraPositionState.locationTrackingMode,
                        )
                    )
                },
                onFailure = {
                    viewModel.onIntent(
                        MapIntent.ChangeRunningMode(
                            true,
                            null,
                            cameraPositionState.locationTrackingMode,
                        )
                    )
                },
            )
        },
    )
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
private fun MapScreen(
    uiState: MapState,
    cameraPositionState: CameraPositionState,
    locationSource: LocationSource,
    onLocationChange: (LatLng) -> Unit,
    onFabClick: () -> Unit,
) {
    val runningMode = uiState.runningMode

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
//        key(uiState.mapResetTrigger) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties =
                MapProperties(
                    minZoom = MapConstants.MIN_ZOOM_LEVEL,
                    maxZoom = MapConstants.MAX_ZOOM_LEVEL,
                    locationTrackingMode = LocationTrackingMode.Follow,
                    isNightModeEnabled = isSystemInDarkTheme(),
                    mapType = MapType.Basic,
                ),
            uiSettings =
                MapUiSettings(
                    isZoomGesturesEnabled = true,
                    isZoomControlEnabled = false,
                    isLocationButtonEnabled = true,
                    isLogoClickEnabled = false,
                    isScaleBarEnabled = false,
                    isCompassEnabled = false,
                ),
            locationSource = locationSource,
            onLocationChange = { location ->
                onLocationChange(LatLng(location))
            },
        ) {
            if (runningMode && uiState.path.size >= MIN_LENGTH_PATH_ARRAY) {
                PathOverlay(
                    coords = uiState.path,
                    color = Color.Magenta,
                    width = 5.dp,
                )
            }
        }
//        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(end = 48.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FloatingActionButton(
                    containerColor = Color(0xFFABABE7),
                    onClick = {
                        onFabClick()
                    },
                ) {
                    Icon(
                        imageVector =
                            if (!runningMode) {
                                Icons.Default.PlayArrow
                            } else {
                                Icons.Default.Stop
                            },
                        contentDescription = "Start Running Button",
                        tint = Color.Black,
                    )
                }
            }
        }
    }
}

fun FusedLocationProviderClient.fetchLastLocation(
    context: Context,
    onSuccess: (location: Location) -> Unit,
    onFailure: () -> Unit,
) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        this.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    onSuccess(location)
                } else {
                    onFailure()
                }
            }.addOnFailureListener {
                onFailure()
            }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Preview
@Composable
private fun MapScreenPreview() {
    MapScreen(
        uiState = MapState(),
        cameraPositionState = rememberCameraPositionState(),
        locationSource = rememberFusedLocationSource(),
        onLocationChange = {
        },
        onFabClick = {
        },
    )
}
