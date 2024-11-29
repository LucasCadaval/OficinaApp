package com.example.oficina.ui.ordens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.oficina.models.Cliente
import kotlinx.coroutines.launch

@Composable
fun ClientSelectionDialog(
    viewModel: OrdemServicoViewModel,
    onClientSelected: (Cliente) -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val isLoadingClientes by viewModel.isLoadingClientes.collectAsState()
    val clientesError by viewModel.clientesError.collectAsState()
    val searchClientesResults by viewModel.searchClientesResults.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Selecionar Cliente", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                var clienteSearchQuery by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = clienteSearchQuery,
                    onValueChange = { query ->
                        clienteSearchQuery = query
                        viewModel.searchClientesByNome(query)
                    },
                    label = { Text("Nome do Cliente") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { /* Optional action */ }) {
                            Icon(Icons.Filled.Search, contentDescription = "Buscar Cliente")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (isLoadingClientes) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
                clientesError?.let { error ->
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(searchClientesResults) { cliente ->
                        ClienteItem(
                            cliente = cliente,
                            onClick = {
                                onClientSelected(cliente)
                                viewModel.clearSearchClientes()
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        onDismiss()
                        viewModel.clearSearchClientes()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}
