package com.example.oficina.ui.veiculos

import androidx.compose.foundation.clickable
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
import com.example.oficina.models.Veiculo
import com.example.oficina.ui.clientes.ClienteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeiculosScreen(
    viewModel: VeiculosViewModel,
    onAddVeiculo: () -> Unit,
    onVeiculoClick: (Veiculo) -> Unit
) {
    val veiculos by viewModel.veiculos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Veículos") },
                actions = {
                    IconButton(onClick = onAddVeiculo) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Novo Veículo"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
//        LazyColumn(modifier = Modifier.padding(innerPadding)) {
//            items(veiculos) { veiculo ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable { onVeiculoClick(veiculo) } // Chama a função de callback ao clicar
//                        .padding(16.dp)
//                ) {
//                    Text(veiculo.nome)
//                }
//            }
//        }
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
                veiculos.isEmpty() -> {
                    // Mensagem para Lista Vazia
                    Text(
                        text = "Nenhum veículo encontrado.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    // Lista de Veiculos em Cards
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(veiculos) { veiculo ->
                            VeiculoCard(
                                veiculo = veiculo,
                                onClick = { onVeiculoClick(veiculo) }
                            )
                        }
                    }
                }
            }
        }
    }
}

