package com.example.formulariokotlin.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.formulariokotlin.navigation.AppNavHost
import com.example.formulariokotlin.ui.theme.FormularioKotlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FormularioKotlinTheme {
                AppNavHost()
            }
        }
    }
}
