package ru.netology.nework.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: Date,
    val likedByMe: Boolean = false,
    val likeOwnerIds: List<Long> = emptyList(),
    val mentionIds: List<Long> = emptyList(),
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
    val authorJob: String? = null
) : Parcelable

@Parcelize
data class Attachment(
    val url: String,
    val type: String
) : Parcelable