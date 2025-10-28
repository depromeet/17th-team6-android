package com.dpm.sixpack.presentation.routes.feed.contract.uistate

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.ReactingUserState
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class ReactionDetailsUiState(
    val allUsersSortedByTime: List<ReactingUserState> = listOf(),
    val usersByEmoji: Map<Emoji, List<ReactingUserState>> = mapOf(),
    val selectedType: String = "",
): Parcelable
