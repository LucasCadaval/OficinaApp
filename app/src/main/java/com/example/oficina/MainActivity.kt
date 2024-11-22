package com.example.oficina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.oficina.ui.login.LoginScreen
import com.example.oficina.ui.main.MainScreen
import com.example.oficina.ui.register.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isLoggedIn by remember { mutableStateOf(false) }
            var isRegisterScreen by remember { mutableStateOf(false) }

            when {
                isLoggedIn -> MainScreen()
                isRegisterScreen -> RegisterScreen(onRegisterSuccess = { isRegisterScreen = false })
                else -> LoginScreen(
                    onLoginSuccess = { isLoggedIn = true },
                    onNavigateToRegister = { isRegisterScreen = true }
                )
            }
        }
    }
}
