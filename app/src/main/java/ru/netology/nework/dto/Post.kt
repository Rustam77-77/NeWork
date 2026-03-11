package ru.netology.nework.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
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
    val likeOwnerIds: List<Long>?,
    val likedByMe: Boolean?,
    val ownedByMe: Boolean?,
    val attachment: Attachment?,
    val mentionIds: List<Long>?,
    val mentionedMe: Boolean?
) : Parcelable