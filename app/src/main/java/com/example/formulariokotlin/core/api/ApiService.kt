package com.example.formulariokotlin.core.api

import com.example.formulariokotlin.features.login.data.models.LoginResponse
import com.example.formulariokotlin.features.login.data.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class ApiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val users = listOf(
        User("yael1@mail.com", "password0"),
        User("yael2@mail.com", "password00"),
        User("yael3@mail.com", "password000")
    )

    suspend fun login(email: String, password: String): Result<LoginResponse> = withContext(Dispatchers.IO) {
        try {
            withTimeout(5000L) {
                val user = users.find { it.email == email && it.password == password }

                return@withTimeout if (user != null) {
                    Result.success(LoginResponse(true, "Login successful", "token"))
                } else {
                    Result.failure(Exception("Invalid credentials"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}