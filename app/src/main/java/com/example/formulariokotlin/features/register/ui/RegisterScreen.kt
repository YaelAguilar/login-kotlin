package com.example.formulariokotlin.features.register.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.formulariokotlin.features.register.viewmodel.RegisterState
import com.example.formulariokotlin.features.register.viewmodel.RegisterViewModel
import com.example.formulariokotlin.ui.theme.DarkGray
import com.example.formulariokotlin.ui.theme.Orange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val context = LocalContext.current
    val registerState by viewModel.registerState.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Success -> {
                Toast.makeText(context, (registerState as RegisterState.Success).message, Toast.LENGTH_SHORT).show()
                onRegisterSuccess()
                viewModel.resetRegisterState()
            }
            is RegisterState.Error -> {
                Toast.makeText(context, (registerState as RegisterState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetRegisterState()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGray)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registro",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Crea tu cuenta de FNC.",
                fontSize = 14.sp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Nombre", color = Color.White, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Tu nombre", color = Color.Gray) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Orange,
                    unfocusedBorderColor = Orange,
                    cursorColor = Orange,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Email", color = Color.White, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("correo@gmail.com", color = Color.Gray) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Orange,
                    unfocusedBorderColor = Orange,
                    cursorColor = Orange,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Password", color = Color.White, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Crea tu contraseña", color = Color.Gray) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Orange,
                    unfocusedBorderColor = Orange,
                    cursorColor = Orange,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.register(name, email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Registrar", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text(text = "¿Ya tienes cuenta? ", color = Color.LightGray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Inicia sesión",
                    color = Orange,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        onGoToLogin()
                    }
                )
            }
        }
    }
}
