package com.dpm.sixpack.presentation.routes.feed.certifiablerecord.contract

import com.dpm.sixpack.presentation.common.base.SideEffect
import com.dpm.sixpack.presentation.common.model.RecordItem

sealed interface CertifiableRecordSideEffect : SideEffect {
    data object NavigateBack : CertifiableRecordSideEffect

    data class NavigateToPostUpload(
        val record: RecordItem,
    ) : CertifiableRecordSideEffect

    data object ShowNoRecordSelectedError : CertifiableRecordSideEffect
}
