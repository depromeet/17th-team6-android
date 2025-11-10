package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.model.RunSession
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.util.DoRunResult
import timber.log.Timber
import java.time.YearMonth
import javax.inject.Inject

class GetRunSessionsUseCase
    @Inject
    constructor(
        private val runningSessionRepository: RunningSessionRepository,
    ) {
        suspend operator fun invoke(
            yearMonth: Pair<Int, Int>? = null,
            isSelfied: Boolean? = null,
        ): DoRunResult<List<RunSession>> {
            // Convert yearMonth to startDateTime and endDateTime
            val (startDateTime, endDateTime) =
                yearMonth?.let { (year, month) ->
                    val ym = YearMonth.of(year, month)
                    val lastDay = ym.lengthOfMonth()

                    // startDateTime: 해당 월의 첫째 날 00:00:00
                    val start = String.format("%04d-%02d-01T00:00:00", year, month)
                    // endDateTime: 해당 월의 마지막 날 23:59:59
                    val end = String.format("%04d-%02d-%02dT23:59:59", year, month, lastDay)

                    Timber.d("GetRunSessions - yearMonth: ($year, $month) → startDateTime: $start, endDateTime: $end")
                    Pair(start, end)
                } ?: Pair(null, null)

            Timber.d("GetRunSessions - Calling repository with isSelfied: $isSelfied, startDateTime: $startDateTime, endDateTime: $endDateTime")

            return runningSessionRepository.getRunSessions(
                isSelfied = isSelfied,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
            )
        }
    }
