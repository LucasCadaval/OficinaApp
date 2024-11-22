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

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome do Veículo") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = cor, onValueChange = { cor = it }, label = { Text("Cor") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = placa,
            onValueChange = { placa = applyPlacaMask(it) },
            label = { Text("Placa") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val veiculo = Veiculo(nome = nome, marca = marca, cor = cor, placa = placa)
            viewModel.addVeiculo(veiculo, onBack)
        }) {
            Text("Salvar Veículo")
        }
    }
}
