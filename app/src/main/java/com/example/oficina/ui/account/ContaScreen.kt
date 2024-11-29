package com.example.oficina.ui.account

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oficina.data.AppDatabase
import com.example.oficina.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ContaScreen(context: Context, onLogout: () -> Unit) {
    var email by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val user: User? = AppDatabase.getDatabase(context).userDao().getLoggedInUser()
            email = user?.email
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (email != null) {
                Text(text = "Bem-vindo, $email!")
            } else {
                Text(text = "Bem-vindo!")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        AppDatabase.getDatabase(context).userDao().clearUser()
                        onLogout()
                    }
                }
            ) {
                Text("Sair")
            }
        }
    }
}
