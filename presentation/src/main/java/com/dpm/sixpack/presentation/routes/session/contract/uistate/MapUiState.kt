package com.dpm.sixpack.presentation.routes.session.contract.uistate

import android.os.Parcelable
import com.naver.maps.geometry.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapUiState(
    val paceColors: List<List<Int>> = listOf(),
    val path: List<List<LatLng>> = listOf(),
) : Parcelable
