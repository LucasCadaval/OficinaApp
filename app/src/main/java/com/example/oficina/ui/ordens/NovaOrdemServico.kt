package com.example.oficina.ui.ordens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.oficina.models.Cliente
import com.example.oficina.models.OrdemServico
import com.example.oficina.models.Peca
import com.example.oficina.models.Status
import com.example.oficina.utils.applyPlacaMask
import kotlinx.coroutines.launch

@Composable
fun NovaOrdemServico(
    viewModel: OrdemServicoViewModel,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()

    // Form states
    var selectedCliente by remember { mutableStateOf<Cliente?>(null) }
    var problema by remember { mutableStateOf("") }
    var pecas by remember { mutableStateOf(listOf<Peca>()) }
    var novaPecaNome by remember { mutableStateOf("") }
    var novaPecaValor by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(Status.ABERTA) }
    val veiculos = remember { mutableStateListOf<String>() }

    // Vehicle search states
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchError by viewModel.searchError.collectAsState()

    // Client selection dialog state
    var isDialogOpen by remember { mutableStateOf(false) }

    // Function to calculate total value
    fun calcularValorTotal(): Double {
        val pecasTotal = pecas.sumOf { it.valor }
        return 150.0 + pecasTotal
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Ensures proper spacing
    ) {
        // Header Section
        item {
            Text(
                text = "Nova Ordem de Serviço",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Client Selection
        item {
            OutlinedTextField(
                value = selectedCliente?.nome ?: "",
                onValueChange = {},
                label = { Text("Cliente") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isDialogOpen = true },
                enabled = false,
                trailingIcon = {
                    IconButton(onClick = { isDialogOpen = true }) {
                        Icon(Icons.Filled.Search, contentDescription = "Buscar Cliente")
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Status Selection
        item {
            DropdownMenuBox(selectedStatus = selectedStatus, onStatusSelected = { novoStatus ->
                selectedStatus = novoStatus
            })
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Vehicle List Section
        item {
            Text("Veículos Selecionados:")
            if (veiculos.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 150.dp) // Constrain height to prevent infinite constraints
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
        }

        // Vehicle Search Section
        item {
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
        }

        // Loading Indicator or Search Error
        item {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            searchError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        // Search Results
        if (searchResults.isNotEmpty()) {
            item {
                Text("Resultados da Busca:")
                Spacer(modifier = Modifier.height(4.dp))
            }
            items(searchResults) { veiculo ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            veiculos.add(veiculo.placa)
                            searchQuery = ""
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
        } else if (!isLoading && searchQuery.isNotBlank() && searchResults.isEmpty()) {
            item {
                Text("Nenhum veículo encontrado com a placa \"$searchQuery\".")
            }
        }

        // Problem Description
        item {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = problema,
                onValueChange = { problema = it },
                label = { Text("Problema") },
                modifier = Modifier
                    .fillMaxWidth(),
                maxLines = Int.MAX_VALUE
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Parts List
        itemsIndexed(pecas) { index, peca ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Nome: ${peca.nome}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Valor: R$${String.format("%.2f", peca.valor)}", style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = {
                    pecas = pecas.toMutableList().also { it.removeAt(index) }
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Remover Peça")
                }
            }
        }

        // Add New Part Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Adicionar Peça:", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = novaPecaNome,
                    onValueChange = { novaPecaNome = it },
                    label = { Text("Nome da Peça") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = novaPecaValor,
                    onValueChange = { novaPecaValor = it },
                    label = { Text("Valor (R$)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val valor = novaPecaValor.toDoubleOrNull()
                        if (novaPecaNome.isNotBlank() && valor != null && valor > 0) {
                            pecas = pecas + Peca(nome = novaPecaNome, valor = valor)
                            novaPecaNome = ""
                            novaPecaValor = ""
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Por favor, preencha corretamente a peça.")
                            }
                        }
                    },
                    enabled = novaPecaNome.isNotBlank() && novaPecaValor.toDoubleOrNull() != null && novaPecaValor.toDouble() > 0
                ) {
                    Text("Adicionar")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Total Value
        item {
            Text(
                text = "Valor Total: R$${String.format("%.2f", calcularValorTotal())}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Save Button
        item {
            Button(
                onClick = {
                    if (selectedCliente != null && problema.isNotBlank()) {
                        val ordem = OrdemServico(
                            id = "",
                            clienteId = selectedCliente!!.id,
                            clienteNome = selectedCliente!!.nome,
                            problema = problema,
                            pecas = pecas,
                            veiculos = veiculos.toList(),
                            valorTotal = calcularValorTotal(),
                            status = selectedStatus
                        )
                        coroutineScope.launch {
                            viewModel.saveOrdemServico(
                                ordem,
                                onComplete = {
                                    onBack()
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Ordem de Serviço salva com sucesso!")
                                    }
                                },
                                onFailure = { e ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Erro ao salvar ordem de serviço: ${e.message}")
                                    }
                                }
                            )
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Por favor, selecione um cliente e descreva o problema.")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedCliente != null && problema.isNotBlank()
            ) {
                Text("Salvar Ordem de Serviço")
            }
        }

        // Client Selection Dialog
        if (isDialogOpen) {
            item {
                ClientSelectionDialog(
                    viewModel = viewModel,
                    onClientSelected = { cliente ->
                        selectedCliente = cliente
                        isDialogOpen = false
                    },
                    onDismiss = { isDialogOpen = false }
                )
            }
        }
    }
}
