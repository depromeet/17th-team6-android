package com.dpm.sixpack.presentation.routes.feed.contract.uistate

import androidx.compose.runtime.Immutable
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.ReactingUserUiState

@Immutable
data class ReactionDetailsUiState(
    val allUsersSortedByTime: List<ReactingUserUiState>,
    val usersByEmoji: Map<Emoji, List<ReactingUserUiState>>,
    val selectedType : String = ""
)
