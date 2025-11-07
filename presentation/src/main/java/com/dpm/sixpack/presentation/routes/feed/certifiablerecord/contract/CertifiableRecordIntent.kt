package com.dpm.sixpack.presentation.routes.feed.certifiablerecord.contract

import com.dpm.sixpack.presentation.common.base.UiIntent
import com.dpm.sixpack.presentation.common.model.RecordItem

sealed interface CertifiableRecordIntent : UiIntent {
    data object OnBackClick : CertifiableRecordIntent

    data class OnRecordClick(
        val record: RecordItem,
    ) : CertifiableRecordIntent

    data object OnUploadClick : CertifiableRecordIntent
}
