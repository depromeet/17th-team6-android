package com.dpm.sixpack.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dpm.sixpack.presentation.common.permission.PermissionHandler
import com.dpm.sixpack.presentation.map.component.MapConstants
import com.dpm.sixpack.presentation.map.component.MapConstants.DEFAULT_ZOOM
import com.dpm.sixpack.presentation.map.contract.MapIntent
import com.dpm.sixpack.presentation.map.contract.MapSideEffect
import com.dpm.sixpack.presentation.map.contract.MapState
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.LocationSource
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapType
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapRoute(
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Location
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationSource = rememberFusedLocationSource()

    // State
    val uiState = viewModel.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = uiState.value.cameraPosition
    }

    PermissionHandler(
        context = context,
        lifecycleOwner = lifecycleOwner,
        permissionsToRequest = MapConstants.MAP_PERMISSIONS,
        onPermissionResult = { isGranted ->
            viewModel.onIntent(MapIntent.UpdateLocationPermission(isGranted))
        },
    )

    viewModel.collectSideEffect { mapSideEffect ->
        when (mapSideEffect) {
            is MapSideEffect.ShowToast -> {

            }

            is MapSideEffect.MoveCameraToPosition -> {
                cameraPositionState.position = CameraPosition(mapSideEffect.latLng, DEFAULT_ZOOM)
            }

            is MapSideEffect.SetInitialLocation -> {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    ) == PackageManager.PERMISSION_GRANTED && !uiState.value.isInitialLocationSet
                ) {
                    // 권한이 허용되었으면
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            val userLatLng = LatLng(location)
                            viewModel.onIntent(MapIntent.SetInitialLocation(userLatLng))
                            cameraPositionState.position = CameraPosition(userLatLng, DEFAULT_ZOOM)
                        } else {
                            println()
                        }
                    }
                }
            }
        }
    }

    MapScreen(
        state = uiState.value,
        cameraPositionState = cameraPositionState,
        locationSource = locationSource,
    )
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
private fun MapScreen(
    state: MapState,
    cameraPositionState: CameraPositionState,
    locationSource: LocationSource,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                locationTrackingMode = LocationTrackingMode.Follow,
                isNightModeEnabled = isSystemInDarkTheme(),
                mapType = MapType.Basic,
            ),
            uiSettings = MapUiSettings(
                isZoomGesturesEnabled = true,
                isZoomControlEnabled = false,
                isLocationButtonEnabled = true,
                isLogoClickEnabled = false,
                isScaleBarEnabled = false,
                isCompassEnabled = false,
            ),
            locationSource = locationSource,
        ) {

        }
    }
}
