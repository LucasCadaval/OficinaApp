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
    // Estado para armazenar o email do usuário
    var email by remember { mutableStateOf<String?>(null) }

    // CoroutineScope para operações assíncronas
    val coroutineScope = rememberCoroutineScope()

    // Recuperar o email do usuário logado quando o Composable for iniciado
    LaunchedEffect(Unit) {
        // Executar a operação no dispatcher IO para evitar bloqueio da UI
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
            // Exibir o email se estiver disponível
            if (email != null) {
                Text(text = "Bem-vindo, $email!")
            } else {
                Text(text = "Bem-vindo!")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        // Limpar o usuário logado do banco de dados
                        AppDatabase.getDatabase(context).userDao().clearUser()
                        // Chamar a função de logout
                        onLogout()
                    }
                }
            ) {
                Text("Sair")
            }
        }
    }
}
