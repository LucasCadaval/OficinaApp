package com.example.oficina.ui.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var loginErrorMessage: String? = null
        private set

    fun loginWithEmailAndPassword(email: String, password: String, onLoginResult: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onLoginResult(true)
                } else {
                    loginErrorMessage = task.exception?.localizedMessage ?: "Erro desconhecido"
                    onLoginResult(false)
                }
            }
    }
}
