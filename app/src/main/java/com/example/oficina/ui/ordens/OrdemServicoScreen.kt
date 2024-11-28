package com.example.oficina.ui.ordens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oficina.models.OrdemServico
import com.example.oficina.models.Status
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdemServicoScreen(
    viewModel: OrdemServicoViewModel,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // State for the list of orders
    val ordens by viewModel.ordens.collectAsState()
    val filtroAtual by viewModel.filtro.collectAsState()

    // State to control which screen to show
    var currentScreen by remember { mutableStateOf("list") }
    var ordemSelecionada by remember { mutableStateOf<OrdemServico?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Ordens de Serviço") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                ordemSelecionada = null
                currentScreen = "create"
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar Ordem de Serviço")
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            "list" -> {
                // Display the list of orders
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    item {
                        // Filter Component
                        FiltroOrdenServico(
                            filtroAtual = filtroAtual,
                            onFiltroSelecionado = { novoFiltro ->
                                viewModel.setFiltro(novoFiltro)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(ordens) { ordem ->
                        OrdemServicoCard(
                            ordem = ordem,
                            onEdit = {
                                ordemSelecionada = ordem
                                currentScreen = "edit"
                            },
                            onDelete = { ordemId ->
                                coroutineScope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Deseja excluir esta ordem?",
                                        actionLabel = "Excluir",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.excluirOrdemServico(ordemId,
                                            onComplete = {
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Ordem excluída com sucesso!")
                                                }
                                            },
                                            onFailure = { e ->
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Erro ao excluir ordem: ${e.message}")
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
            "create" -> {
                // Navigate to NovaOrdemServico
                NovaOrdemServico(
                    viewModel = viewModel,
                    onBack = { currentScreen = "list" },
                    snackbarHostState = snackbarHostState
                )
            }
            "edit" -> {
                // Navigate to EditOrdemServico
                EditOrdemServico(
                    viewModel = viewModel,
                    ordemSelecionada = ordemSelecionada,
                    onBack = {
                        ordemSelecionada = null
                        currentScreen = "list"
                    },
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}
