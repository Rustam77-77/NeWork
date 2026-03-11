package ru.netology.nework.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class EventType {
    @SerializedName("offline")
    OFFLINE,

    @SerializedName("online")
    ONLINE
}

data class Event(
    val id: Long,
    val authorId: Long,
<<<<<<< HEAD
    val author: String?,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String?,
    val datetime: String?,
    val published: String?,
    val coords: Coordinates?,
    val type: EventType?,
    val link: String?,
    val speakerIds: List<Long>? = emptyList(),
    val speakers: List<UserPreview>? = emptyList(),
    val participantIds: List<Long>? = emptyList(),
    val participants: List<UserPreview>? = emptyList(),
    val attachment: Attachment?,
    val likeOwnerIds: List<Long>? = emptyList(),
    val likedByMe: Boolean?,
    val likes: Int? = likeOwnerIds?.size ?: 0
=======
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coordinates?,
    val type: EventType? = EventType.ONLINE, // Оставляем nullable с дефолтным значением
    val link: String?,
    val speakerIds: List<Long> = emptyList(),
    val speakers: List<UserPreview> = emptyList(),
    val participantIds: List<Long> = emptyList(),
    val participants: List<UserPreview> = emptyList(),
    val attachment: Attachment?,
    val likeOwnerIds: List<Long> = emptyList(),
    val likedByMe: Boolean = false,
    val likes: Int = likeOwnerIds.size
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
) : Serializable