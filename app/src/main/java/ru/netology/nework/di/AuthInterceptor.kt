package ru.netology.nework.di

import okhttp3.Interceptor
import okhttp3.Response
import ru.netology.nework.data.repository.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenManager.getToken()

        val request = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", token)  // Убрано "Bearer "
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }
}