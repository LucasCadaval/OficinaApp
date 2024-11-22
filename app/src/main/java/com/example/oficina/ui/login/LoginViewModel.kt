package com.example.oficina.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oficina.data.AppDatabase
import com.example.oficina.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun loginWithEmailAndPassword(
        context: Context,
        email: String,
        password: String,
        onLoginResult: (Boolean) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        val user = User(id = auth.currentUser!!.uid, email = email)
                        AppDatabase.getDatabase(context).userDao().insertUser(user)
                        onLoginResult(true)
                    }
                } else {
                    onLoginResult(false)
                }
            }
    }
}
