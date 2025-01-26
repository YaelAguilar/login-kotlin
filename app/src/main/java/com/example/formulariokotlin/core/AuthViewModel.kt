package com.example.formulariokotlin.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    sealed class AuthUIState {
        data object Idle : AuthUIState()
        data object Loading : AuthUIState()
        data class Success(val token: String) : AuthUIState()
        data class Error(val message: String) : AuthUIState()
    }

    private val _uiState = MutableStateFlow<AuthUIState>(AuthUIState.Idle)

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUIState.Loading
            // Simula una llamada de red
            kotlinx.coroutines.delay(2000)
            if (email == "user@example.com" && password == "password") {
                _token.value = "fake_token_123"
                _uiState.value = AuthUIState.Success("fake_token_123")
            } else {
                _uiState.value = AuthUIState.Error("Credenciales inválidas.")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUIState.Loading
            kotlinx.coroutines.delay(1000)
            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                _uiState.value = AuthUIState.Success("Registro exitoso.")
            } else {
                _uiState.value = AuthUIState.Error("Por favor, completa todos los campos.")
            }
        }
    }

    /**
     * Almacena un nuevo token.
     */
    fun setToken(newToken: String?) {
        _token.value = newToken
    }

    /**
     * Limpia el token almacenado.
     */
    fun clearToken() {
        _token.value = null
    }
}
