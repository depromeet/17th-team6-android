package com.dpm.sixpack.data.source.remote.dto.response

import com.dpm.sixpack.domain.model.ReactionResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReactionResultDto(
    @SerialName("selfieId")
    val selfieId: Int,
    @SerialName("emojiType")
    val emojiType: String,
    @SerialName("action")
    val action: String,
    @SerialName("totalReactionCount")
    val totalReactionCount: Int,
) {
    fun toDomain(): ReactionResult =
        ReactionResult(
            selfieId = selfieId,
            emojiType = emojiType,
            action = action,
            totalReactionCount = totalReactionCount,
        )
}
