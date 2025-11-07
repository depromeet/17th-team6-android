package com.dpm.sixpack.data.source.local.database

import com.dpm.sixpack.data.source.local.database.entity.RunningTrackPointEntity
import com.dpm.sixpack.domain.model.MaxPaceData
import com.dpm.sixpack.domain.model.RealtimeRunningData

fun RealtimeRunningData.toTrackPointEntity(): RunningTrackPointEntity =
    RunningTrackPointEntity(
        timestamp = timestamp,
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        speed = speed,
        avgPace = avgPace,
        maxPace = maxPace.value,
        maxPaceLatitude = maxPace.latitude,
        maxPaceLongitude = maxPace.longitude,
        avgCadence = avgCadence,
        maxCadence = maxCadence,
        distanceInMeter = distanceInMeter,
        durationInSec = durationInSec,
    )

fun RunningTrackPointEntity.toRealtimeRunningData(): RealtimeRunningData =
    RealtimeRunningData(
        timestamp = timestamp,
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        speed = speed,
        avgPace = avgPace,
        maxPace =
            MaxPaceData(
                value = maxPace,
                latitude = maxPaceLatitude,
                longitude = maxPaceLongitude,
            ),
        avgCadence = avgCadence,
        maxCadence = maxCadence,
        distanceInMeter = distanceInMeter,
        durationInSec = durationInSec,
    )
