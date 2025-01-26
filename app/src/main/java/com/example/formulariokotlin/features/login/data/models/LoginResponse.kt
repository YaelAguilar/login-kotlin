package com.example.formulariokotlin.features.login.data.models

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null
)
