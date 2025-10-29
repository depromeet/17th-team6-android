package com.dpm.sixpack.data.source.local.database

import com.dpm.sixpack.data.source.local.database.entity.RunningTrackPointEntity
import com.dpm.sixpack.domain.model.RealtimeRunningData

fun RealtimeRunningData.toTrackPointEntity(): RunningTrackPointEntity =
    RunningTrackPointEntity(
        timestamp = timestamp,
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        speed = speed,
        avgPace = avgPace,
        avgCadence = avgCadence,
        distanceInMeter = distanceInMeter,
    )
