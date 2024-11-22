package com.example.oficina.ui.clientes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clientes") },
                actions = {
                    Button(onClick = onAddCliente) {
                        Text("Novo Cliente")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(clientes) { cliente ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClienteClick(cliente) }
                        .padding(16.dp)
                ) {
                    Text(cliente.nome)
                }
            }
        }
    }
}
