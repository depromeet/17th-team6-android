package com.dpm.sixpack.presentation.common.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.dpm.sixpack.presentation.R

@Immutable
data class PostReactionUiState(
    val emoji: Emoji,
    val count: String,
    val isReacted: Boolean = false,
)

@Immutable
data class ReactingUserUiState(
    val user: UserUiState,
    val reactedAt: String,
    val emoji: Emoji = Emoji.UNKNOWN,
)

enum class Emoji(
    val type: String,
) {
    LIKE("like"),
    HEART("heart"),
    FIRE("fire"),
    SHOOT("shoot"),
    SAD("sad"),
    UNKNOWN("unknown"),
    ;

    val iconRes: Int
        @DrawableRes
        get() =
            when (this) {
                LIKE -> R.drawable.ill_endurance
                HEART -> R.drawable.ill_endurance
                FIRE -> R.drawable.ill_endurance
                SHOOT -> R.drawable.ill_endurance
                SAD -> R.drawable.ill_endurance
                UNKNOWN -> R.drawable.ic_add_reaction
            }

    companion object {
        fun from(type: String?): Emoji = entries.find { it.name.equals(type, ignoreCase = true) } ?: UNKNOWN
    }
}
