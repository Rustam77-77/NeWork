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
    val authorName: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: Date,
    val eventDate: Date,
    val type: EventType,
    val likedByMe: Boolean = false,
    val likesCount: Int = 0,
    val link: String? = null,
    val participants: List<Long> = emptyList(),
    val speakers: List<Long> = emptyList(),
    val authorJob: String? = null
)

fun EventEntity.toModel(): Event {
    return Event(
        id = id,
        authorId = authorId,
        authorName = authorName,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        eventDate = eventDate,
        type = type,
        likedByMe = likedByMe,
        likesCount = likesCount,
        link = link,
        participants = participants,
        speakers = speakers,
        authorJob = authorJob
    )
}

fun Event.toEntity(): EventEntity {
    return EventEntity(
        id = id,
        authorId = authorId,
        authorName = authorName,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        eventDate = eventDate,
        type = type,
        likedByMe = likedByMe,
        likesCount = likesCount,
        link = link,
        participants = participants,
        speakers = speakers,
        authorJob = authorJob
    )
}