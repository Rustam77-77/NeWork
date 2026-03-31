package ru.netology.nework.dto

import android.os.Parcelable
import com.google.gson.annotations.JsonAdapter
import kotlinx.parcelize.Parcelize
import ru.netology.nework.utils.InstantAdapter
import java.time.Instant

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
    @JsonAdapter(InstantAdapter::class)
    val published: Instant,
    @JsonAdapter(InstantAdapter::class)
    val datetime: Instant,
    val type: EventType,
    val likedByMe: Boolean = false,
    val likeOwnerIds: List<Long> = emptyList(),
    val link: String? = null,
    val participantIds: List<Long> = emptyList(),
    val speakerIds: List<Long> = emptyList(),
    val ownedByMe: Boolean = false,
    val authorJob: String? = null
) : Parcelable