package com.dpm.sixpack.presentation.common.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class UserState(
    val id: Long,
    val name: String,
    val profileImageUrl: String,
    val isMe: Boolean = false,
): Parcelable
