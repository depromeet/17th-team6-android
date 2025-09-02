package com.dpm.sixpack.presentation.util

import com.naver.maps.geometry.LatLng

fun calculateDistance(
    toLat: Double,
    toLng: Double,
    fromLat: Double,
    fromLng: Double,
): Double = calculateDistance(
    LatLng(toLat, toLng),
    LatLng(fromLat, fromLng),
)

fun calculateDistance(
    toLatLng: LatLng,
    fromLatLng: LatLng,
): Double = fromLatLng.distanceTo(toLatLng)

