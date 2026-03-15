package ru.netology.nework.dto
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
enum class EventType { OFFLINE, ONLINE }
enum class AttachmentType { IMAGE, VIDEO, AUDIO }
@Parcelize
data class Attachment(
    val url: String,
    val type: AttachmentType,
) : Parcelable
@Parcelize
data class Coordinates(
    val lat: Double,
    val long: Double,
) : Parcelable
@Parcelize
data class Event(
    val id: Long,
    val authorId: Long = 0,
    val author: String = "",
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String = "",
    val datetime: String = "",
    val published: String = "",
    val type: EventType? = EventType.OFFLINE,
    val coords: Coordinates? = null,
    val link: String? = null,
    val speakerIds: Set<Long> = emptySet(),
    val participantsIds: Set<Long> = emptySet(),
    val participatedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val likedByMe: Boolean = false,
    val likeOwnerIds: Set<Long> = emptySet(),
    val ownedByMe: Boolean = false,
) : Parcelable
data class AuthState(
    val id: Long = 0,
    val token: String? = null
)