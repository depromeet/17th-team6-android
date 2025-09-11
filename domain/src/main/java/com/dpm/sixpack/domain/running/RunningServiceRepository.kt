package com.dpm.sixpack.domain.running

import com.dpm.sixpack.domain.model.RunningState
import kotlinx.coroutines.flow.StateFlow

interface RunningServiceRepository {
    val runningDataState: StateFlow<RunningState>
}
