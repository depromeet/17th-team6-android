package com.dpm.sixpack.presentation.common.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.dpm.sixpack.presentation.R
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class PostReactionState(
    val emoji: Emoji,
    val count: String,
    val isReacted: Boolean = false,
) : Parcelable

@Immutable
@Parcelize
data class ReactingUserState(
    val user: UserState,
    val reactedAt: String,
    val emoji: Emoji = Emoji.UNKNOWN,
) : Parcelable

@Parcelize
enum class Emoji(
    val type: String,
) : Parcelable {
    SURPRISE("surprise"),
    HEART("heart"),
    THUMBS_UP("thumbs_up"),
    CONGRATS("congrats"),
    FIRE("fire"),
    UNKNOWN("unknown"),
    ;

    val iconRes: Int
        @DrawableRes
        get() =
            when (this) {
                SURPRISE -> R.drawable.ill_endurance // 적절한 아이콘 리소스로 변경 필요
                HEART -> R.drawable.ill_endurance
                THUMBS_UP -> R.drawable.ill_endurance // 적절한 아이콘 리소스로 변경 필요
                CONGRATS -> R.drawable.ill_endurance // 적절한 아이콘 리소스로 변경 필요
                FIRE -> R.drawable.ill_endurance
                UNKNOWN -> R.drawable.ic_add_reaction
            }

    companion object {
        fun from(type: String?): Emoji = entries.find { it.name.equals(type, ignoreCase = true) } ?: UNKNOWN
    }
}
