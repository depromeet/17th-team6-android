package com.dpm.sixpack.presentation.common.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.dpm.sixpack.presentation.common.util.format.toDateWithDayOfWeekOrNull
import com.dpm.sixpack.presentation.common.util.format.toTimeOnlyOrNull
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
@Immutable
data class RecordItem(
    val sessionId: Long = 0,
    val runningSummary: RunningSummary = RunningSummary(),
    val mapImageUrl: String = "",
    val isPosted: Boolean = false,
    val postTime: String = "",
) : Parcelable {
    val formattedTime: String
        get() = postTime.toTimeOnlyOrNull() ?: LocalDate.now().toString()
    val formattedDate: String
        get() = postTime.toDateWithDayOfWeekOrNull() ?: LocalDate.now().toString()
}
