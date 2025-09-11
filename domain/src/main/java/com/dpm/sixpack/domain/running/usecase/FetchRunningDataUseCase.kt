package com.dpm.sixpack.domain.running.usecase

import com.dpm.sixpack.domain.running.RunningServiceRepository
import javax.inject.Inject

class FetchRunningDataUseCase @Inject constructor(
    private val repository: RunningServiceRepository,
) {
    operator fun invoke() = repository.runningDataState
}
