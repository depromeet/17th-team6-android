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
    val users: List<ReactingUserInfo> = emptyList(),
) : Parcelable

@Immutable
@Parcelize
data class ReactingUserInfo(
    val user: UserInfo,
    val reactedAt: String,
    val emoji: Emoji,
) : Parcelable

// TODO 슬랙보고 string 바꾸기
@Parcelize
enum class Emoji(
    val type: String,
) : Parcelable {
    SURPRISE("SURPRISE"),
    HEART("HEART"),
    THUMBS_UP("THUMBS_UP"),
    CONGRATS("CONGRATS"),
    FIRE("FIRE"),
    ;

    val iconRes: Int
        @DrawableRes
        get() =
            when (this) {
                SURPRISE -> R.drawable.ic_reaction_surprise // 적절한 아이콘 리소스로 변경 필요
                HEART -> R.drawable.ic_reaction_heart
                THUMBS_UP -> R.drawable.ic_reaction_thumbs_up // 적절한 아이콘 리소스로 변경 필요
                CONGRATS -> R.drawable.ic_reaction_congrats // 적절한 아이콘 리소스로 변경 필요
                FIRE -> R.drawable.ic_reaction_fire
            }

    companion object {
        fun from(type: String?): Emoji = entries.find { it.name.equals(type, ignoreCase = true) } ?: HEART
    }
}

fun Reaction.toPostReaction(): PostReaction {
    val emoji = Emoji.from(this.emojiType)

    return PostReaction(
        emoji = emoji,
        count = this.totalCount.toString(),
        isReacted = this.isReacted,
        users = this.users.map { it.toReactingUserInfo(emoji) },
    )
}

fun ReactingUser.toReactingUserInfo(emoji: Emoji): ReactingUserInfo =
    ReactingUserInfo(
        user = user.toUserInfo(),
        reactedAt = this.reactedAt,
        emoji = emoji,
    )
