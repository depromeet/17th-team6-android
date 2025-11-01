package com.dpm.sixpack.presentation.routes.feed.contract.uistate

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.PostReaction
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
sealed interface ReactionDetailsUiState : Parcelable {
    @Parcelize
    data object Loading : ReactionDetailsUiState

    @Parcelize
    data class Success(
        val reactions: List<PostReaction>,

        val selectedEmoji: Emoji,
    ) : ReactionDetailsUiState {
        val totalSize: Int
            get() = reactions.sumOf { it.count.toInt() }
    }
}
