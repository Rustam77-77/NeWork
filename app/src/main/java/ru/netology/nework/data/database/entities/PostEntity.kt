package ru.netology.nework.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.dto.Post
import java.util.Date

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val authorId: Long,
    val authorName: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: Date,
    val likedByMe: Boolean = false,
    val likesCount: Int = 0,
    val link: String? = null,
    val mentionedUsers: List<Long> = emptyList(),
    val authorJob: String? = null
)

fun PostEntity.toModel(): Post {
    return Post(
        id = id,
        authorId = authorId,
        authorName = authorName,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likesCount = likesCount,
        link = link,
        mentionedUsers = mentionedUsers,
        authorJob = authorJob
    )
}

fun Post.toEntity(): PostEntity {
    return PostEntity(
        id = id,
        authorId = authorId,
        authorName = authorName,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likesCount = likesCount,
        link = link,
        mentionedUsers = mentionedUsers,
        authorJob = authorJob
    )
}