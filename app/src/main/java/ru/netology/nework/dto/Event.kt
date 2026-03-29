package ru.netology.nework.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

enum class EventType {
    ONLINE, OFFLINE
}

@Parcelize
data class Event(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: Date,
    val datetime: Date,
    val type: EventType,
    val likedByMe: Boolean = false,
    val likeOwnerIds: List<Long> = emptyList(),
    val link: String? = null,
    val participantIds: List<Long> = emptyList(),
    val speakerIds: List<Long> = emptyList(),
    val ownedByMe: Boolean = false,
    val authorJob: String? = null
) : Parcelable