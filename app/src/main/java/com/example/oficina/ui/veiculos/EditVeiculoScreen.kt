package com.example.oficina.ui.veiculos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.oficina.models.Veiculo
import com.example.oficina.utils.applyPlacaMask
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVeiculoScreen(
    veiculo: Veiculo,
    viewModel: VeiculosViewModel,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var nome by remember { mutableStateOf(veiculo.nome) }
    var marca by remember { mutableStateOf(veiculo.marca) }
    var cor by remember { mutableStateOf(veiculo.cor) }
    var placa by remember { mutableStateOf(veiculo.placa) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Campos do Formulário
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome do Veículo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = marca,
            onValueChange = { marca = it },
            label = { Text("Marca") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = cor,
            onValueChange = { cor = it },
            label = { Text("Cor") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = placa,
            onValueChange = { placa = applyPlacaMask(it) },
            label = { Text("Placa") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Exibe mensagem de erro, se houver
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Botão para Salvar Alterações
        Button(
            onClick = {
                if (nome.isBlank() || marca.isBlank() || cor.isBlank() || placa.isBlank()) {
                    errorMessage = "Todos os campos devem ser preenchidos."
                } else {
                    val veiculoAtualizado = Veiculo(
                        id = veiculo.id,
                        nome = nome,
                        marca = marca,
                        cor = cor,
                        placa = placa
                    )
                    coroutineScope.launch {
                        viewModel.updateVeiculo(
                            veiculo.id,
                            veiculoAtualizado,
                            onComplete = { onBack() },
                            onFailure = { e ->
                                errorMessage = "Erro ao salvar alterações: ${e.message}"
                            }
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = nome.isNotBlank() && marca.isNotBlank() && cor.isNotBlank() && placa.isNotBlank()
        ) {
            Text("Salvar Alterações")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão para Excluir Veículo
        Button(
            onClick = {
                coroutineScope.launch {
                    viewModel.deleteVeiculo(
                        veiculo.id,
                        onComplete = { onBack() },
                        onFailure = { e ->
                            errorMessage = "Erro ao excluir veículo: ${e.message}"
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Excluir Veículo")
        }
    }
}


