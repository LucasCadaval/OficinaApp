package com.example.oficina.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var isLoading = false
        private set

    var isLoginSuccessful = false
        private set

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            delay(2000) // Simula uma chamada de API
            isLoading = false
            isLoginSuccessful = email == "admin@example.com" && password == "123456"
            onResult(isLoginSuccessful)
        }
    }
}
