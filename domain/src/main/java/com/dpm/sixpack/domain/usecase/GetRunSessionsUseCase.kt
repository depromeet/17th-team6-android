package com.dpm.sixpack.domain.usecase

import com.dpm.sixpack.domain.model.RunSession
import com.dpm.sixpack.domain.repository.RunningSessionRepository
import com.dpm.sixpack.domain.util.DoRunResult
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
            // Convert yearMonth to startDateTime (first day of the month at 00:00:00)
            val startDateTime =
                yearMonth?.let { (year, month) ->
                    // Format: 2025-09-01T00:00:00
                    String.format("%04d-%02d-01T00:00:00", year, month)
                }

            return runningSessionRepository.getRunSessions(
                isSelfied = isSelfied,
                startDateTime = startDateTime,
            )
        }
    }
