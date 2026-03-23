package ru.netology.nework.dto

import java.util.Date

data class Post(
    val id: Long,
    val authorId: Long,
    val authorName: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: Date,
    val likedByMe: Boolean = false,
    val likesCount: Int = 0,
    val link: String? = null,
    val mentionedUsers: List<Long> = emptyList(),
    val authorJob: String? = null
)