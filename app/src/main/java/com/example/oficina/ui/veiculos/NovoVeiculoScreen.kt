package com.example.oficina.ui.veiculos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.oficina.models.Veiculo
import com.example.oficina.utils.applyPlacaMask

@Composable
fun NovoVeiculoScreen(viewModel: VeiculosViewModel, onBack: () -> Unit) {
    var nome by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var cor by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome do Veículo") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = cor, onValueChange = { cor = it }, label = { Text("Cor") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = placa,
            onValueChange = { placa = applyPlacaMask(it) },
            label = { Text("Placa") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (nome.isBlank() || marca.isBlank() || cor.isBlank() || placa.isBlank()) {
                errorMessage = "Todos os campos devem ser preenchidos."
            } else {
                val veiculo = Veiculo(nome = nome, marca = marca, cor = cor, placa = placa)
                viewModel.addVeiculo(
                    veiculo,
                    onComplete = {
                        errorMessage = null
                        onBack()
                    },
                    onFailure = { e ->
                        errorMessage = "Erro ao salvar veículo: ${e.message}"
                    }
                )
            }
        }) {
            Text("Salvar Veículo")
        }
    }
}
