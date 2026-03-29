package ru.netology.nework.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import java.util.Date

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
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
)

fun EventEntity.toModel(): Event {
    return Event(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        datetime = datetime,
        type = type,
        likedByMe = likedByMe,
        likeOwnerIds = likeOwnerIds,
        link = link,
        participantIds = participantIds,
        speakerIds = speakerIds,
        ownedByMe = ownedByMe,
        authorJob = authorJob
    )
}

fun Event.toEntity(): EventEntity {
    return EventEntity(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        datetime = datetime,
        type = type,
        likedByMe = likedByMe,
        likeOwnerIds = likeOwnerIds,
        link = link,
        participantIds = participantIds,
        speakerIds = speakerIds,
        ownedByMe = ownedByMe,
        authorJob = authorJob
    )
}