package com.example.formulariokotlin.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.formulariokotlin.navigation.AppNavHost
import com.example.formulariokotlin.ui.theme.DarkOrangeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Aplicamos el tema oscuro + naranja
            DarkOrangeTheme {
                AppNavHost()
            }
        }
    }
}
