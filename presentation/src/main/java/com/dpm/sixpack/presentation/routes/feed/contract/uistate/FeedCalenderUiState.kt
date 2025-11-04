package com.dpm.sixpack.presentation.routes.feed.contract.uistate

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
@Immutable
data class FeedCalenderUiState(
    val isLoading: Boolean = false,
    val today: LocalDate = LocalDate.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val postCounts: Map<LocalDate, Int> = mapOf(LocalDate.now() to 3),
) : Parcelable
