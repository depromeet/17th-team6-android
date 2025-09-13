package com.dpm.sixpack.data.source.local.gps

import com.google.android.gms.location.FusedLocationProviderClient
import javax.inject.Inject

class LocationDataSource
    @Inject
    constructor(
        private val locationClient: FusedLocationProviderClient,
    )
