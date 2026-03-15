package ru.netology.nework.repository
import ru.netology.nework.api.UsersApi
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.User
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class UserRepository @Inject constructor(
    private val usersApi: UsersApi
) {
    suspend fun getAll(): List<User> =
        usersApi.getAll().body() ?: emptyList()

    suspend fun getUserWall(userId: Long): List<Post> =
        usersApi.getUserWall(userId).body() ?: emptyList()
    suspend fun getUserJobs(userId: Long): List<Job> =
        usersApi.getJobs(userId).body() ?: emptyList()
    suspend fun saveJob(job: Job) {
        usersApi.saveJob(job)
    }
    suspend fun deleteJob(id: Long) {
        usersApi.deleteJob(id)
    }
}