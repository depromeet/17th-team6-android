package com.dpm.sixpack.presentation.routes.feed.contract.uistate

import androidx.compose.runtime.Immutable
import java.time.LocalDate

@Immutable
data class FeedCalenderUiState(
    val today: LocalDate = LocalDate.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val postCounts: Map<LocalDate, Int> = emptyMap(),
)
