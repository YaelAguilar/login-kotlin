package com.example.formulariokotlin.features.register.data

import com.example.formulariokotlin.core.api.ApiService
import com.example.formulariokotlin.features.register.data.models.RegisterResponse

class RegisterRepository(private val apiService: ApiService) {
    suspend fun register(name: String, email: String, password: String): Result<RegisterResponse> {
        return apiService.register(name, email, password)
    }
}
