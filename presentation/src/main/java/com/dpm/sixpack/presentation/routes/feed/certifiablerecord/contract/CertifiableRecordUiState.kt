package com.dpm.sixpack.presentation.routes.feed.certifiablerecord.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.common.model.RecordItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class CertifiableRecordUiState(
    val records: List<RecordItem> = emptyList(),
    val selectedRecord: RecordItem? = null,
    val isLoading: Boolean = false,
) : UiState,
    Parcelable
