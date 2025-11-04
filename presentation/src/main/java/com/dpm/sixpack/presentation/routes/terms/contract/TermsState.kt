package com.dpm.sixpack.presentation.routes.terms.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.routes.terms.ui.component.model.TermType
import kotlinx.parcelize.Parcelize

@Parcelize
data class TermsState(
    val termsState: Map<TermType, Boolean> = TermType.entries.associateWith { false },
    val isLoading: Boolean = false,
) : UiState,
    Parcelable {
    val isAllRequiredTermsAgreed: Boolean
        get() =
            TermType.entries
                .filter { it.isRequired }
                .all { termsState[it] == true }

    val isAllTermsAgreed: Boolean
        get() = TermType.entries.all { termsState[it] == true }
}
