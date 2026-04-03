package ru.netology.nework.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.dto.Job
import java.time.Instant

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val name: String,
    val position: String,
    val start: Instant,  // Поле должно называться start
    val finish: Instant? = null,
    val link: String? = null
)

fun JobEntity.toModel(): Job {
    return Job(
        id = id,
        userId = userId,
        name = name,
        position = position,
        start = start,
        finish = finish,
        link = link
    )
}

fun Job.toEntity(): JobEntity {
    return JobEntity(
        id = id,
        userId = userId,
        name = name,
        position = position,
        start = start,
        finish = finish,
        link = link
    )
}