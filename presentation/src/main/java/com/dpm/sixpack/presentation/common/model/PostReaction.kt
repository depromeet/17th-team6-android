package com.dpm.sixpack.presentation.common.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.dpm.sixpack.domain.model.ReactingUser
import com.dpm.sixpack.domain.model.Reaction
import com.dpm.sixpack.presentation.R
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class PostReaction(
    val emoji: Emoji,
    val count: String,
    val isReacted: Boolean = false,
    val users : List<ReactingUserInfo> = emptyList()
) : Parcelable

@Immutable
@Parcelize
data class ReactingUserInfo(
    val user: UserInfo,
    val reactedAt: String,
    val emoji: Emoji = Emoji.ALL
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
    ALL("all"),
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
                ALL -> R.drawable.ic_add_reaction
            }

    companion object {
        fun from(type: String?): Emoji = entries.find { it.name.equals(type, ignoreCase = true) } ?: ALL
    }
}

fun Reaction.toPostReaction(): PostReaction {
    val emoji = Emoji.from(this.emojiType)

    return PostReaction(
        emoji = emoji,
        count = this.totalCount.toString(),
        isReacted = this.isReacted,
        users = this.users.map { it.toReactingUserInfo(emoji) }
    )
}


fun ReactingUser.toReactingUserInfo(emoji: Emoji): ReactingUserInfo {
    return ReactingUserInfo(
        user = user.toUserInfo(),
        reactedAt = this.reactedAt,
        emoji = emoji
    )
}
