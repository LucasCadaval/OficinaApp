package com.example.oficina.ui.clientes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.oficina.models.Cliente
import com.example.oficina.models.Veiculo
import com.example.oficina.utils.applyCepMask
import com.example.oficina.utils.applyCpfMask
import com.example.oficina.utils.applyPlacaMask
import kotlinx.coroutines.launch

@Composable
fun NovoClienteScreen(viewModel: ClientesViewModel, onBack: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var cep by remember { mutableStateOf("") }
    var endereco by remember { mutableStateOf("") }
    var cidade by remember { mutableStateOf("") }
    var veiculoPlaca by remember { mutableStateOf("") }
    val veiculos = remember { mutableStateListOf<String>() }
    var contato by remember { mutableStateOf("") }

    // Estados para o buscador de veículos
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchError by viewModel.searchError.collectAsState()

    // Envolve todo o conteúdo em uma coluna rolável para suportar telas menores ou muito conteúdo
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())  // Adiciona rolagem vertical
    ) {
        // Campos do Formulário
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome Completo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = cpf,
            onValueChange = { cpf = applyCpfMask(it) },
            label = { Text("CPF") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = cep,
            onValueChange = { cep = applyCepMask(it) },
            label = { Text("CEP") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = endereco,
            onValueChange = { endereco = it },
            label = { Text("Endereço") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = cidade,
            onValueChange = { cidade = it },
            label = { Text("Cidade") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = contato,
            onValueChange = { contato = it },
            label = { Text("Contato") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Lista de Veículos Selecionados
        Text("Veículos Selecionados:")
        if (veiculos.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 150.dp)  // Define uma altura máxima
                    .fillMaxWidth()
            ) {
                items(veiculos) { placa ->
                    Text(" - $placa")
                }
            }
        } else {
            Text("Nenhum veículo selecionado.")
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Buscador de Veículos
        Text("Buscar Veículo por Placa:")
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = applyPlacaMask(query)
                },
                label = { Text("Placa do Veículo") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (searchQuery.isNotBlank()) {
                        coroutineScope.launch {
                            viewModel.searchVeiculosByPlaca(searchQuery)
                        }
                    }
                },
                enabled = !isLoading && searchQuery.isNotBlank(),
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("Buscar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Exibir Indicador de Carregamento
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Exibir Erro de Busca
        searchError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Exibir Resultados da Busca
        if (searchResults.isNotEmpty()) {
            Text("Resultados da Busca:")
            Spacer(modifier = Modifier.height(4.dp))
            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 200.dp)
                    .fillMaxWidth()
            ) {
                items(searchResults) { veiculo ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                veiculos.add(veiculo.placa)
                                searchQuery = ""
                                // Limpa os resultados após a seleção chamando o método do ViewModel
                                viewModel.clearSearchResults()
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Placa: ${veiculo.placa}", style = MaterialTheme.typography.bodyLarge)
                            Text("Nome: ${veiculo.nome}", style = MaterialTheme.typography.bodyMedium)
                            Text("Marca: ${veiculo.marca}", style = MaterialTheme.typography.bodyMedium)
                            Text("Cor: ${veiculo.cor}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        } else if (!isLoading && searchQuery.isNotBlank() && searchResults.isEmpty()) {
            Text("Nenhum veículo encontrado com a placa \"$searchQuery\".")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão para Salvar Cliente
        Button(
            onClick = {
                // Validações básicas (pode ser expandido conforme necessário)
                if (nome.isBlank() || cpf.isBlank() || cep.isBlank() || endereco.isBlank() || cidade.isBlank() || contato.isBlank()) {
                    // Exibir mensagem de erro ou feedback ao usuário
                    // Implementação opcional
                } else {
                    val cliente = Cliente(
                        nome = nome,
                        cpf = cpf,
                        cep = cep,
                        endereco = endereco,
                        cidade = cidade,
                        contato = contato,
                        veiculos = veiculos.toList()
                    )
                    coroutineScope.launch {
                        viewModel.addCliente(
                            cliente,
                            onComplete = { onBack() },
                            onFailure = { e ->
                                // Trate o erro, por exemplo, exiba uma mensagem ao usuário
                                // Implementação opcional
                            }
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = nome.isNotBlank() && cpf.isNotBlank() && cep.isNotBlank() && endereco.isNotBlank() && cidade.isNotBlank() && contato.isNotBlank()
        ) {
            Text("Salvar Cliente")
        }
    }
}
