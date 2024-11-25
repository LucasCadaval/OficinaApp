// ClientesScreen.kt

package com.example.oficina.ui.clientes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oficina.models.Cliente

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesScreen(
    viewModel: ClientesViewModel,
    onAddCliente: () -> Unit,
    onClienteClick: (Cliente) -> Unit
) {
    val clientes by viewModel.clientes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clientes") },
                actions = {
                    IconButton(onClick = onAddCliente) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Novo Cliente"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when {
                isLoading -> {
                    // Indicador de Carregamento
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    // Exibir Mensagem de Erro
                    Text(
                        text = error ?: "Erro desconhecido",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                clientes.isEmpty() -> {
                    // Mensagem para Lista Vazia
                    Text(
                        text = "Nenhum cliente encontrado.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    // Lista de Clientes em Cards
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(clientes) { cliente ->
                            ClienteCard(
                                cliente = cliente,
                                onClick = { onClienteClick(cliente) }
                            )
                        }
                    }
                }
            }
        }
    }
}
