package com.example.formulariokotlin.features.market.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.formulariokotlin.ui.theme.DarkGray
import com.example.formulariokotlin.ui.theme.Orange

@Composable
fun MarketScreen(
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGray)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MARKET",
                fontSize = 28.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { onLogout() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Cerrar sesión")
            }
        }
    }
}
