package com.example.oficina.ui.veiculos

import androidx.compose.foundation.clickable
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
fun VeiculosScreen(
    viewModel: VeiculosViewModel,
    onAddVeiculo: () -> Unit,
    onVeiculoClick: (Veiculo) -> Unit
) {
    val veiculos by viewModel.veiculos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Veículos") },
                actions = {
                    Button(onClick = onAddVeiculo) {
                        Text("Novo Veículo")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(veiculos) { veiculo ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onVeiculoClick(veiculo) } // Chama a função de callback ao clicar
                        .padding(16.dp)
                ) {
                    Text(veiculo.nome)
                }
            }
        }
    }
}

