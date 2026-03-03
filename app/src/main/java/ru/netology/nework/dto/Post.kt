package ru.netology.nework.dto

import java.io.Serializable

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val published: String,
    val coords: Coordinates?,
    val link: String?,
    val mentionIds: List<Long> = emptyList(),
    val mentionUsers: List<UserPreview> = emptyList(),
    val attachment: Attachment?,
    val likeOwnerIds: List<Long> = emptyList(),
    val likedByMe: Boolean,
    val likes: Int = likeOwnerIds.size
) : Serializable

data class Coordinates(
    val lat: Double,
    val long: Double
) : Serializable

data class UserPreview(
    val id: Long,
    val name: String,
    val avatar: String?
) : Serializable