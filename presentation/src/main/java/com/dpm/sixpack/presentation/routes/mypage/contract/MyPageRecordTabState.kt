package com.dpm.sixpack.presentation.routes.mypage.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyPageRecordTabState(
    val records: List<RecordItem> = emptyList(),
    val currentYearMonth: YearMonth = YearMonth(),
    val isLoading: Boolean = false,
) : UiState,
    Parcelable
