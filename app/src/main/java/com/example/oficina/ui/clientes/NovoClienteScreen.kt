package com.example.oficina.ui.clientes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oficina.models.Cliente
import com.example.oficina.utils.applyCepMask
import com.example.oficina.utils.applyCpfMask
import com.example.oficina.utils.applyPlacaMask

@Composable
fun NovoClienteScreen(viewModel: ClientesViewModel, onBack: () -> Unit) {
    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var cep by remember { mutableStateOf("") }
    var endereco by remember { mutableStateOf("") }
    var cidade by remember { mutableStateOf("") }
    var veiculoPlaca by remember { mutableStateOf("") }
    val veiculos = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome Completo") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = cpf,
            onValueChange = { cpf = applyCpfMask(it) },
            label = { Text("CPF") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = cep,
            onValueChange = { cep = applyCepMask(it) },
            label = { Text("CEP") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = endereco, onValueChange = { endereco = it }, label = { Text("Endereço") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = cidade, onValueChange = { cidade = it }, label = { Text("Cidade") })
        Spacer(modifier = Modifier.height(16.dp))

        Text("Veículos:")
        veiculos.forEach { placa ->
            Text(" - $placa")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            TextField(
                value = veiculoPlaca,
                onValueChange = { veiculoPlaca = applyPlacaMask(it) },
                label = { Text("Placa do Veículo") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (veiculoPlaca.isNotBlank()) {
                    veiculos.add(veiculoPlaca)
                    veiculoPlaca = ""
                }
            }) {
                Text("Adicionar Veículo")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val cliente = Cliente(
                nome = nome,
                cpf = cpf,
                cep = cep,
                endereco = endereco,
                cidade = cidade,
                veiculos = veiculos.toList()
            )
            viewModel.addCliente(cliente, onBack)
        }) {
            Text("Salvar Cliente")
        }
    }
}
