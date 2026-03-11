package ru.netology.nework.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class EventType {
    ONLINE,
    OFFLINE
}

@Parcelize
data class UserPreview(
    val id: Long,
    val name: String,
    val avatar: String?
) : Parcelable

@Parcelize
data class Event(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coordinates?,
    val type: EventType?,
    val link: String?,
    val likeCount: Int?,
    val dislikeCount: Int?,
    val likedByMe: Boolean?,
    val dislikedByMe: Boolean?,
    val ownedByMe: Boolean?,
    val participantsCount: Int?,
    val participatedByMe: Boolean?,
    val attachment: Attachment?,
    val speakerIds: List<Long>?,
    val speakers: List<UserPreview>?,
    val title: String,
    val likeOwnerIds: List<Long>? = emptyList(),
    val mentionIds: List<Long>? = emptyList(),
    val mentionedMe: Boolean? = false
) : Parcelable