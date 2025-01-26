package com.example.formulariokotlin.features.navigation

// Importaciones necesarias
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.formulariokotlin.features.login.ui.LoginScreen
import com.example.formulariokotlin.features.register.ui.RegisterScreen
import com.example.formulariokotlin.features.tasks.ui.MarketScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    // Guardamos el token en un estado mutable local
    var token by remember { mutableStateOf<String?>(null) }

    NavHost(navController = navController, startDestination = "login") {
        // Ruta para la pantalla de Login
        composable("login") {
            LoginScreen(
                onLoginSuccess = { receivedToken ->
                    // Actualizamos el token en el estado local
                    token = receivedToken
                    // Navegamos a la pantalla de Market
                    navController.navigate("market") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onGoToRegister = {
                    // Navegamos a la pantalla de Registro
                    navController.navigate("register")
                }
            )
        }

        // Ruta para la pantalla de Registro
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    // Después de registrarse, navegamos de vuelta a Login
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onGoToLogin = {
                    // Navegamos de vuelta a Login
                    navController.popBackStack()
                }
            )
        }

        // Ruta para la pantalla de Market
        composable("market") {
            MarketScreen(
                onLogout = {
                    // Al cerrar sesión, limpiamos el token y navegamos a Login
                    token = null
                    navController.navigate("login") {
                        popUpTo("market") { inclusive = true }
                    }
                },
                token = token ?: ""
            )
        }
    }
}
