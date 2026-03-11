package ru.netology.nework.di

<<<<<<< HEAD
=======
import ru.netology.nework.BuildConfig
import ru.netology.nework.api.AuthApi
import ru.netology.nework.api.EventsApi
import ru.netology.nework.api.PostsApi
import ru.netology.nework.api.UsersApi
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
<<<<<<< HEAD
import ru.netology.nework.BuildConfig
import ru.netology.nework.api.AuthApi
import ru.netology.nework.api.EventsApi
import ru.netology.nework.api.PostsApi
import ru.netology.nework.api.UsersApi
=======
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val BASE_URL = "http://94.228.125.136:8080/"

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
<<<<<<< HEAD
=======
            .addInterceptor(logging)
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
                    .addHeader("Api-Key", BuildConfig.API_KEY)
<<<<<<< HEAD
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
=======
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")

                val request = requestBuilder.build()
                chain.proceed(request)
            }
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        gson: Gson,
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun providePostsApi(retrofit: Retrofit): PostsApi {
        return retrofit.create(PostsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideEventsApi(retrofit: Retrofit): EventsApi {
        return retrofit.create(EventsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUsersApi(retrofit: Retrofit): UsersApi {
        return retrofit.create(UsersApi::class.java)
    }
}