package com.dpm.sixpack.domain.model

data class ReactionResult(
    val selfieId: Int,
    val emojiType: String,
    val action: String, // "ADDED" or "REMOVED"
    val totalReactionCount: Int
)
