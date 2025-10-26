package com.dpm.sixpack.data.source.local.database

import com.dpm.sixpack.data.source.local.database.entity.RunningTrackPointEntity
import com.dpm.sixpack.domain.model.RunningTrackPoint

fun RunningTrackPoint.toEntity(): RunningTrackPointEntity =
    RunningTrackPointEntity(
        sessionId = this.sessionId,
        timestamp = this.timestamp,
        latitude = this.latitude,
        longitude = this.longitude,
        altitude = this.altitude,
        speed = this.speed,
        avgPace = this.avgPace,
        avgCadence = this.avgCadence,
        distanceInMeter = this.distanceInMeter,
    )
