package com.dpm.sixpack.presentation.common.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.dpm.sixpack.domain.model.CertifiedUser
import com.dpm.sixpack.domain.model.Feed
import com.dpm.sixpack.domain.repository.FeedListItem
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class PostResource(
    val feedId: Long = 0,
    val user: PostingUserInfo = PostingUserInfo(),
    val postImageUrl: String = "",
    val runningInfo: RunningSummary = RunningSummary(),
    val reactions: List<PostReaction> = listOf(),
) : Parcelable

@Parcelize
@Immutable
data class PostingUserInfo(
    val user: UserInfo = UserInfo(),
    val postingTime: String = "",
) : Parcelable

fun FeedListItem.PostItem.toPostResource(): PostResource =
    PostResource(
        feedId = feed.feedId,
        user =
            PostingUserInfo(
                user = feed.user.toUserInfo(),
                postingTime = feed.selfieTime,
            ),
        postImageUrl = feed.imageUrl,
        runningInfo = feed.runningSessionResult.toRunningSummary(feed.selfieTime),
        reactions = feed.reactions.map { it.toPostReaction() },
    )

fun Feed.toPostResource(): PostResource =
    PostResource(
        feedId = feedId,
        user =
            PostingUserInfo(
                user = user.toUserInfo(),
                postingTime = selfieTime,
            ),
        postImageUrl = imageUrl,
        runningInfo = runningSessionResult.toRunningSummary(selfieTime),
        reactions = reactions.map { it.toPostReaction() },
    )

fun CertifiedUser.toPostingUserInfo(): PostingUserInfo =
    PostingUserInfo(
        user = user.toUserInfo(),
        postingTime = postingTime,
    )
