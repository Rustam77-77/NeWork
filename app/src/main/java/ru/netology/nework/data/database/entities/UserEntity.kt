package ru.netology.nework.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.dto.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val login: String,
    val name: String,
    val avatar: String? = null,
    val job: String? = null
)

fun UserEntity.toModel(): User {
    return User(
        id = id,
        login = login,
        name = name,
        avatar = avatar,
        job = job
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        login = login,
        name = name,
        avatar = avatar,
        job = job
    )
}