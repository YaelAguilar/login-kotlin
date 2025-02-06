package com.example.formulariokotlin.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.formulariokotlin.core.AuthViewModel
import com.example.formulariokotlin.features.login.ui.LoginScreen
import com.example.formulariokotlin.features.register.ui.RegisterScreen
import com.example.formulariokotlin.features.tasks.ui.MarketScreen
import com.example.formulariokotlin.features.textrecognition.ui.TextRecognitionScreen

@Composable
fun AppNavHost(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val token by authViewModel.token.collectAsState()

    NavHost(navController = navController, startDestination = "login") {
        // Ruta para la pantalla de Login
        composable("login") {
            LoginScreen(
                onLoginSuccess = { receivedToken ->
                    authViewModel.setToken(receivedToken) // Almacenar el token en AuthViewModel
                    navController.navigate("market") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onGoToRegister = {
                    navController.navigate("register")
                }
            )
        }

        // Ruta para la pantalla de Registro
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onGoToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Ruta para la pantalla de Market
        composable("market") {
            MarketScreen(
                onLogout = {
                    authViewModel.clearToken() // Limpiar el token al cerrar sesión
                    navController.navigate("login") {
                        popUpTo("market") { inclusive = true }
                    }
                },
                token = token ?: "",
                onOpenTextRecognition = {
                    navController.navigate("text_recognition")
                }
            )
        }

        // Ruta para reconocimiento de texto
        composable("text_recognition") {
            TextRecognitionScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
