package com.example.formulariokotlin.core

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import com.example.formulariokotlin.features.login.ui.LoginScreen
import com.example.formulariokotlin.ui.theme.FormulariokotlinTheme
import com.example.formulariokotlin.features.login.viewmodel.LoginState
import com.example.formulariokotlin.features.login.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FormulariokotlinTheme {
                val loginState = viewModel.loginState.collectAsState()

                LoginScreen(
                    onLoginClick = { email, password ->
                        viewModel.login(email, password)
                    }
                )

                // Observar el estado del login
                when (val state = loginState.value) {
                    is LoginState.Success -> {
                        Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    }
                    is LoginState.Error -> {
                        Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    }
                    is LoginState.Loading -> {
                    }
                    else -> {}
                }
            }
        }
    }
}
