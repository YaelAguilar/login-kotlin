package com.example.formulariokotlin.features.login.data

import com.example.formulariokotlin.core.api.ApiService
import com.example.formulariokotlin.features.login.data.models.LoginResponse

class LoginRepository(private val apiService: ApiService) {
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return apiService.login(email, password)
    }
}