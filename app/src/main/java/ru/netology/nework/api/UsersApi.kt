package ru.netology.nework.api
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.User
interface UsersApi {
    @GET("users")
    suspend fun getAll(): Response<List<User>>
    @GET("{userId}/jobs")
    suspend fun getJobs(@Path("userId") userId: Long): Response<List<Job>>
    @POST("my/jobs") // Путь для сохранения вашей работы
    suspend fun saveJob(@Body job: Job): Response<Job>
    @DELETE("my/jobs/{id}") // Путь для удаления работы
    suspend fun deleteJob(@Path("id") id: Long): Response<Unit>
    @GET("{userId}/wall")
    suspend fun getUserWall(@Path("userId") userId: Long): Response<List<Post>>
}