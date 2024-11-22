package com.example.oficina.ui.veiculos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oficina.models.Veiculo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeiculosScreen(viewModel: VeiculosViewModel, onAddVeiculo: () -> Unit) {
    val veiculos by viewModel.veiculos.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Veículos") },
            actions = {
                Button(onClick = onAddVeiculo) {
                    Text("Novo Veículo")
                }
            }
        )
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(veiculos) { veiculo ->
                Text(veiculo.nome)
            }
        }
    }
}
