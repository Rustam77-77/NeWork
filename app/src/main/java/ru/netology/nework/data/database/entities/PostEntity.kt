package ru.netology.nework.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.Post
import java.time.Instant

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: Instant,
    val likedByMe: Boolean = false,
    val likeOwnerIds: List<Long> = emptyList(),
    val mentionIds: List<Long> = emptyList(),
    val attachment: String? = null,
    val ownedByMe: Boolean = false,
    val authorJob: String? = null
)

fun PostEntity.toModel(): Post {
    return Post(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likeOwnerIds = likeOwnerIds,
        mentionIds = mentionIds,
        attachment = if (!attachment.isNullOrEmpty()) {
            Attachment(attachment, "image")
        } else null,
        ownedByMe = ownedByMe,
        authorJob = authorJob
    )
}

fun Post.toEntity(): PostEntity {
    return PostEntity(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likeOwnerIds = likeOwnerIds,
        mentionIds = mentionIds,
        attachment = attachment?.url,
        ownedByMe = ownedByMe,
        authorJob = authorJob
    )
}