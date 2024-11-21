package com.example.oficina.ui.register

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class RegisterViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var registerErrorMessage: String? = null
        private set

    fun registerWithEmailAndPassword(email: String, password: String, onRegisterResult: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onRegisterResult(true)
                } else {
                    registerErrorMessage = task.exception?.localizedMessage ?: "Erro desconhecido"
                    onRegisterResult(false)
                }
            }
    }
}
