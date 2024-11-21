package com.example.oficina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.oficina.ui.login.LoginScreen
import com.example.oficina.ui.main.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isLoggedIn by remember { mutableStateOf(false) }

            if (isLoggedIn) {
                MainScreen()
            } else {
                LoginScreen(onLoginSuccess = { isLoggedIn = true })
            }
        }
    }
}
