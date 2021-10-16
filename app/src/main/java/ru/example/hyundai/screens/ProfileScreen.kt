package ru.example.hyundai.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun ProfileScreen() {
    Scaffold {
        Column {
            TopAppBar {
                Text(
                    text = "Профиль",
                    modifier = Modifier.padding(horizontal = Dp(16f))
                )
            }

            Text("Профиль")
        }
    }
}