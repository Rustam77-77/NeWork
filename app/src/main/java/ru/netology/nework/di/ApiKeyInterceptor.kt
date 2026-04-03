package ru.netology.nework.di

import okhttp3.Interceptor
import okhttp3.Response
import ru.netology.nework.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val request = originalRequest.newBuilder()
            .header("API-KEY", BuildConfig.API_KEY)
            .build()

        return chain.proceed(request)
    }
}