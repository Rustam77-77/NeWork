package ru.netology.nework.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nework.api.ApiService
import ru.netology.nework.data.database.dao.JobDao
import ru.netology.nework.data.database.entities.JobEntity
import ru.netology.nework.dto.Job
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "JobRepository"

@Singleton
class JobRepository @Inject constructor(
    private val jobDao: JobDao,
    private val apiService: ApiService
) {
    fun getJobsForUser(userId: Long): Flow<List<Job>> =
        jobDao.getJobsForUser(userId).map { entities ->
            entities.map { entity ->
                Job(
                    id = entity.id,
                    userId = entity.userId,
                    company = entity.company,
                    position = entity.position,
                    startDate = entity.startDate,
                    endDate = entity.endDate
                )
            }
        }

    suspend fun refreshJobsForUser(userId: Long) {
        try {
            Log.d(TAG, "refreshJobsForUser: userId=$userId")
            val response = apiService.getJobsForUser(userId)
            Log.d(TAG, "refreshJobsForUser: response code = ${response.code()}")

            if (response.isSuccessful) {
                response.body()?.let { jobs ->
                    Log.d(TAG, "refreshJobsForUser: received ${jobs.size} jobs")

                    val entities = jobs.map { job ->
                        JobEntity(
                            id = job.id,
                            userId = job.userId,
                            company = job.company,
                            position = job.position,
                            startDate = job.startDate,
                            endDate = job.endDate
                        )
                    }
                    jobDao.insertAll(entities)
                    Log.d(TAG, "refreshJobsForUser: saved ${entities.size} jobs to DB")
                }
            } else {
                Log.e(TAG, "refreshJobsForUser: error ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "refreshJobsForUser: exception", e)
        }
    }

    suspend fun createJob(job: Job): Job? {
        return try {
            Log.d(TAG, "createJob: userId=${job.userId}")
            val response = apiService.createJob(job)
            if (response.isSuccessful) {
                response.body()?.also { createdJob ->
                    val entity = JobEntity(
                        id = createdJob.id,
                        userId = createdJob.userId,
                        company = createdJob.company,
                        position = createdJob.position,
                        startDate = createdJob.startDate,
                        endDate = createdJob.endDate
                    )
                    jobDao.insert(entity)
                    Log.d(TAG, "createJob: success")
                }
            } else {
                Log.e(TAG, "createJob: error ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "createJob: exception", e)
            null
        }
    }

    suspend fun deleteJob(jobId: Long): Boolean {
        return try {
            Log.d(TAG, "deleteJob: jobId=$jobId")
            val response = apiService.deleteJob(jobId)
            if (response.isSuccessful) {
                jobDao.deleteById(jobId)
                Log.d(TAG, "deleteJob: success")
                true
            } else {
                Log.e(TAG, "deleteJob: error ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "deleteJob: exception", e)
            false
        }
    }
}