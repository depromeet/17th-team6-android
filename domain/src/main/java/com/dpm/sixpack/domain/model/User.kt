package com.dpm.sixpack.domain.model

data class User(
    val userId: Long, // 123
    val nickName: String, // "해준"
    val isMe: Boolean, // true
    val profileImgUrl: String, // "https://example.com/profile.jpg",
)
