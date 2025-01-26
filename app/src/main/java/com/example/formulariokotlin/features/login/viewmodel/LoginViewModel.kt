package com.example.formulariokotlin.features.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.formulariokotlin.core.api.ApiService
import com.example.formulariokotlin.features.login.data.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = LoginRepository(ApiService())

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            repository.login(email, password)
                .onSuccess { response ->
                    if (response.success) {
                        _loginState.value = LoginState.Success(response.message)
                    } else {
                        _loginState.value = LoginState.Error(response.message)
                    }
                }
                .onFailure { exception ->
                    _loginState.value = LoginState.Error(exception.message ?: "Unknown error")
                }
        }
    }

    // Para resetear el estado y no re-disparar toasts
    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }
}
