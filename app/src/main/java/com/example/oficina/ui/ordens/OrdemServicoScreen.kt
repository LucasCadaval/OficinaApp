package com.example.oficina.ui.ordens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdemServicoScreen(
    viewModel: OrdemServicoViewModel,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Estados do formulário
    var selectedCliente by remember { mutableStateOf<Cliente?>(null) }
    var problema by remember { mutableStateOf("") }
    var pecas by remember { mutableStateOf(listOf<Peca>()) }
    var novaPecaNome by remember { mutableStateOf("") }
    var novaPecaValor by remember { mutableStateOf("") }

    // Estado para controlar se está adicionando uma nova ordem
    var isAddingNewOrdem by remember { mutableStateOf(false) }

    // Estados de diálogo para seleção de cliente
    var isDialogOpen by remember { mutableStateOf(false) }

    // Estados para edição de uma ordem existente
    var ordemSelecionada by remember { mutableStateOf<OrdemServico?>(null) }

    // Função para calcular o valor total
    fun calcularValorTotal(): Double {
        val pecasTotal = pecas.sumOf { it.valor }
        return 150.0 + pecasTotal
    }

    // Função para abrir o formulário para criar uma nova ordem
    fun openNewOrdem() {
        ordemSelecionada = null
        selectedCliente = null
        problema = ""
        pecas = emptyList()
        isAddingNewOrdem = true
    }

    // Função para abrir o formulário para editar uma ordem existente
    fun openEditOrdem(ordem: OrdemServico) {
        ordemSelecionada = ordem
        selectedCliente = Cliente(id = ordem.clienteId, nome = ordem.clienteNome)
        problema = ordem.problema
        pecas = ordem.pecas
        isAddingNewOrdem = false
    }

    // Atualiza o formulário se uma ordem é selecionada para edição
    LaunchedEffect(ordemSelecionada) {
        if (ordemSelecionada != null) {
            selectedCliente = Cliente(id = ordemSelecionada!!.clienteId, nome = ordemSelecionada!!.clienteNome)
            problema = ordemSelecionada!!.problema
            pecas = ordemSelecionada!!.pecas
            isAddingNewOrdem = false
        }
    }

    // Colete o estado das ordens de serviço
    val ordens by viewModel.ordens.collectAsState()

    // Colete o estado dos resultados da busca de clientes
    val searchResults by viewModel.searchClientesResults.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Ordens de Serviço") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { openNewOrdem() }) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar Ordem de Serviço")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Lista de Ordens de Serviço
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // Faz a lista ocupar o espaço restante
            ) {
                items(ordens) { ordem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { openEditOrdem(ordem) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Cliente: ${ordem.clienteNome}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Problema: ${ordem.problema}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Valor Total: R$${String.format("%.2f", ordem.valorTotal)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Peças:", style = MaterialTheme.typography.bodyMedium)
                            ordem.pecas.forEach { peca ->
                                Text(
                                    text = "- ${peca.nome}: R$${String.format("%.2f", peca.valor)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Formulário para criar ou editar uma ordem de serviço
            if (isAddingNewOrdem || ordemSelecionada != null || selectedCliente != null || problema.isNotBlank() || pecas.isNotEmpty()) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = if (isAddingNewOrdem) "Nova Ordem de Serviço" else "Editar Ordem de Serviço",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Seleção de Cliente
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

                // Problema (caixa de texto expansível)
                OutlinedTextField(
                    value = problema,
                    onValueChange = { problema = it },
                    label = { Text("Problema") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    maxLines = Int.MAX_VALUE
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Lista de Peças
                Text(text = "Peças:", style = MaterialTheme.typography.bodyLarge)
                pecas.forEachIndexed { index, peca ->
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
                            // Remover peça
                            pecas = pecas.toMutableList().also { it.removeAt(index) }
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Remover Peça")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Campos para adicionar uma nova peça
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
                                // Exibir mensagem de erro
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

                // Valor Total
                Text(
                    text = "Valor Total: R$${String.format("%.2f", calcularValorTotal())}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botão para Salvar Ordem de Serviço
                Button(
                    onClick = {
                        if (selectedCliente != null && problema.isNotBlank()) {
                            val ordem = OrdemServico(
                                id = ordemSelecionada?.id ?: "",
                                clienteId = selectedCliente!!.id,
                                clienteNome = selectedCliente!!.nome,
                                problema = problema,
                                pecas = pecas,
                                valorTotal = calcularValorTotal()
                            )
                            coroutineScope.launch {
                                viewModel.saveOrdemServico(
                                    ordem,
                                    onComplete = {
                                        // Limpa o formulário após salvar
                                        openNewOrdem()
                                        isAddingNewOrdem = false
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

            // Diálogo para seleção de cliente
            if (isDialogOpen) {
                Dialog(onDismissRequest = { isDialogOpen = false }) {
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
                            var searchQuery by remember { mutableStateOf("") }
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { query ->
                                    searchQuery = query
                                    viewModel.searchClientesByNome(query)
                                },
                                label = { Text("Nome do Cliente") },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = { /* Implementar ação se necessário */ }) {
                                        Icon(Icons.Filled.Search, contentDescription = "Buscar Cliente")
                                    }
                                },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Text
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // Exibir indicadores de carregamento ou erros
                            if (viewModel.isLoadingClientes.collectAsState().value) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            }
                            viewModel.clientesError.collectAsState().value?.let { error ->
                                Text(text = error, color = MaterialTheme.colorScheme.error)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            // Lista de resultados da busca
                            LazyColumn(modifier = Modifier.height(200.dp)) {
                                items(searchResults) { cliente ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable {
                                                selectedCliente = cliente
                                                isDialogOpen = false
                                                isAddingNewOrdem = false
                                            },
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(text = cliente.nome, style = MaterialTheme.typography.bodyLarge)
                                            Text(text = "CPF: ${cliente.cpf}", style = MaterialTheme.typography.bodyMedium)
                                            Text(text = "Endereço: ${cliente.endereco}, ${cliente.cidade}", style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            // Botão de Cancelar
                            Button(
                                onClick = {
                                    isDialogOpen = false
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
        }
    }
}
