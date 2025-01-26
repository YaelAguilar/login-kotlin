package com.example.formulariokotlin.features.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.formulariokotlin.core.api.ApiService
import com.example.formulariokotlin.features.register.data.RegisterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val repository = RegisterRepository(ApiService())

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            repository.register(name, email, password)
                .onSuccess { response ->
                    if (response.success) {
                        _registerState.value = RegisterState.Success(response.message)
                    } else {
                        _registerState.value = RegisterState.Error(response.message)
                    }
                }
                .onFailure { exception ->
                    _registerState.value = RegisterState.Error(exception.message ?: "Unknown error")
                }
        }
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }
}