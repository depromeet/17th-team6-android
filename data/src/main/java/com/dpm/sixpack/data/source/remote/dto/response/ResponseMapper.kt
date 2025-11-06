package com.dpm.sixpack.data.source.remote.dto.response

import com.dpm.sixpack.domain.model.MaxPaceData
import com.dpm.sixpack.domain.model.RunningSessionResult
import com.dpm.sixpack.domain.usecase.SaveRealtimeRunningDataResult

fun FinishRunningResponseDto.toRunningSessionResult() =
    RunningSessionResult(
        totalDistanceMeter = distanceTotal,
        totalDurationSec = durationTotal,
        avgPace = paceAvg,
        maxPace =
            MaxPaceData(
                value = paceMax,
                latitude = paceMaxLatitude,
                longitude = paceMaxLongitude,
            ),
        avgCadence = cadenceAvg,
        maxCadence = cadenceMax,
    )

fun SaveSegmentResponseDto.toSyncResult() =
    SaveRealtimeRunningDataResult.SyncResult(
        segmentId = segmentId,
        savedCount = savedCount,
    )
