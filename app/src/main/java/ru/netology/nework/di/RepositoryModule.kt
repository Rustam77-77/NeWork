package ru.netology.nework.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nework.api.*
import ru.netology.nework.repository.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApi,
        usersApi: UsersApi
    ): AuthRepository {
        return AuthRepository(authApi, usersApi)
    }

    @Provides
    @Singleton
    fun provideEventRepository(
        eventsApi: EventsApi
    ): EventRepository {
        return EventRepository(eventsApi)
    }

    @Provides
    @Singleton
    fun providePostRepository(
        postsApi: PostsApi
    ): PostRepository {
        return PostRepository(postsApi)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        usersApi: UsersApi
    ): UserRepository {
        return UserRepository(usersApi)
    }
}