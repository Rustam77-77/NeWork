package ru.netology.nework.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.dto.Job
import java.util.Date

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val company: String,
    val position: String,
    val startDate: Date,
    val endDate: Date? = null
)

fun JobEntity.toModel(): Job {
    return Job(
        id = id,
        userId = userId,
        company = company,
        position = position,
        startDate = startDate,
        endDate = endDate
    )
}

fun Job.toEntity(): JobEntity {
    return JobEntity(
        id = id,
        userId = userId,
        company = company,
        position = position,
        startDate = startDate,
        endDate = endDate
    )
}