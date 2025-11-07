package com.dpm.sixpack.presentation.routes.settings.accountinfo.contract

import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountInfoState(
    val userId: Long? = null,
    val nickname: String = "",
    val profileImageUrl: String? = null,
    val code: String = "",
    val phoneNumber: String = "",
    val joinDate: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) : UiState
