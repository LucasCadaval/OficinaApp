package com.example.oficina.ui.clientes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oficina.models.Cliente

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteDetailsScreen(cliente: Cliente, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Cliente") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Nome: ${cliente.nome}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("CPF: ${cliente.cpf}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("CEP: ${cliente.cep}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Endereço: ${cliente.endereco}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Cidade: ${cliente.cidade}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Veículos:")
            cliente.veiculos.forEach { placa ->
                Text(" - $placa")
            }
        }
    }
}
