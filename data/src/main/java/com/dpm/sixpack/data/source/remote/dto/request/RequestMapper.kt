@file:Suppress("ktlint:standard:filename")

package com.dpm.sixpack.data.source.remote.dto.request

import com.dpm.sixpack.domain.model.MaxPaceData
import com.dpm.sixpack.domain.model.RunningSessionResult

fun RunningSessionResultDto.toRunningSessionResult() =
    RunningSessionResult(
        totalDistanceMeter = distance.toInt(),
        totalDurationSec = duration.toInt(),
        avgPace = pace.toInt(),
        maxPace = pace.max.toMaxPaceData(),
        avgCadence = cadence.toInt(),
        maxCadence = cadence.max.value,
    )

fun RunningSessionResult.toDto() =
    RunningSessionResultDto(
        distance = DistanceRequestDto(totalDistanceMeter),
        duration = DurationRequestDto(totalDurationSec),
        pace =
            PaceRequestDto(
                avgPace,
                MaxPaceRequestDto(
                    maxPace.value,
                    maxPace.latitude,
                    maxPace.longitude,
                ),
            ),
        cadence = CadenceRequestDto(avgCadence, MaxCadenceRequestDto(0)),
    )

fun PaceRequestDto.toInt(): Int = this.avg

fun DurationRequestDto.toInt(): Int = this.total

fun DistanceRequestDto.toInt(): Int = this.total

fun CadenceRequestDto.toInt(): Int = this.avg

fun MaxPaceRequestDto.toMaxPaceData() = MaxPaceData(this.value, this.latitude, this.longitude)
