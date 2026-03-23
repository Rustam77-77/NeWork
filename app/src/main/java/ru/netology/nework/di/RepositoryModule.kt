package ru.netology.nework.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nework.api.ApiService
import ru.netology.nework.data.database.dao.*
import ru.netology.nework.data.repository.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(apiService: ApiService): AuthRepository {
        return AuthRepository(apiService)
    }

    @Provides
    @Singleton
    fun providePostRepository(
        postDao: PostDao,
        apiService: ApiService
    ): PostRepository {
        return PostRepository(postDao, apiService)
    }

    @Provides
    @Singleton
    fun provideEventRepository(
        eventDao: EventDao,
        apiService: ApiService
    ): EventRepository {
        return EventRepository(eventDao, apiService)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userDao: UserDao,
        apiService: ApiService
    ): UserRepository {
        return UserRepository(userDao, apiService)
    }

    @Provides
    @Singleton
    fun provideJobRepository(
        jobDao: JobDao,
        apiService: ApiService
    ): JobRepository {
        return JobRepository(jobDao, apiService)
    }
}