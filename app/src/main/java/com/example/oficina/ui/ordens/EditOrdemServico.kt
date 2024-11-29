package com.example.oficina.ui.ordens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.oficina.models.Cliente
import com.example.oficina.models.OrdemServico
import com.example.oficina.models.Peca
import com.example.oficina.models.Status
import kotlinx.coroutines.launch

@Composable
fun EditOrdemServico(
    viewModel: OrdemServicoViewModel,
    ordemSelecionada: OrdemServico?,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()

    var selectedCliente by remember { mutableStateOf<Cliente?>(null) }
    var problema by remember { mutableStateOf("") }
    var pecas by remember { mutableStateOf(listOf<Peca>()) }
    var novaPecaNome by remember { mutableStateOf("") }
    var novaPecaValor by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(Status.ABERTA) }
    val veiculos = remember { mutableStateListOf<String>() }

    LaunchedEffect(ordemSelecionada) {
        if (ordemSelecionada != null) {
            selectedCliente = Cliente(id = ordemSelecionada.clienteId, nome = ordemSelecionada.clienteNome)
            problema = ordemSelecionada.problema
            pecas = ordemSelecionada.pecas
            selectedStatus = ordemSelecionada.status
            veiculos.clear()
            veiculos.addAll(ordemSelecionada.veiculos)
        }
    }

    fun calcularValorTotal(): Double {
        val pecasTotal = pecas.sumOf { it.valor }
        return 150.0 + pecasTotal
    }

    fun gerarRelatorio(): String {
        val valorTotal = calcularValorTotal()
        val listaPecas = pecas.joinToString("\n") { "Peça: ${it.nome} - R$${String.format("%.2f", it.valor)}" }
        return """
            Ordem de Serviço:
            Cliente: ${selectedCliente?.nome}
            Problema: $problema
            Status: $selectedStatus
            Veículos: ${veiculos.joinToString(", ")}
            Peças:
            $listaPecas
            Valor Total: R$${String.format("%.2f", valorTotal)}
        """.trimIndent()
    }

    val context = LocalContext.current
    fun compartilharRelatorio(relatorio: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, relatorio)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(intent, "Compartilhar Relatório"))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Editar Ordem de Serviço",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            OutlinedTextField(
                value = selectedCliente?.nome ?: "",
                onValueChange = {},
                label = { Text("Cliente") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            DropdownMenuBox(selectedStatus = selectedStatus, onStatusSelected = { novoStatus ->
                selectedStatus = novoStatus
            })
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Text("Veículos Selecionados:")
            if (veiculos.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 150.dp)
                        .fillMaxWidth()
                ) {
                    items(veiculos) { placa ->
                        Text(" - $placa")
                    }
                }
            } else {
                Text("Nenhum veículo selecionado.")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            OutlinedTextField(
                value = problema,
                onValueChange = { problema = it },
                label = { Text("Problema") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = Int.MAX_VALUE
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

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
                    // Remove part
                    pecas = pecas.toMutableList().also { it.removeAt(index) }
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Remover Peça")
                }
            }
        }

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

        item {
            Text(
                text = "Valor Total: R$${String.format("%.2f", calcularValorTotal())}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                onClick = {
                    if (selectedCliente != null && problema.isNotBlank()) {
                        val ordem = OrdemServico(
                            id = ordemSelecionada?.id ?: "",
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
                                        snackbarHostState.showSnackbar("Ordem de Serviço atualizada com sucesso!")
                                    }
                                },
                                onFailure = { e ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Erro ao atualizar ordem de serviço: ${e.message}")
                                    }
                                }
                            )
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Por favor, preencha todos os campos obrigatórios.")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedCliente != null && problema.isNotBlank()
            ) {
                Text("Salvar Alterações")
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Button(modifier = Modifier.fillMaxWidth(),
                onClick = {
                val relatorio = gerarRelatorio()
                compartilharRelatorio(relatorio)
            }) {
                Text("Gerar e Compartilhar Relatório")
            }
        }
    }
}

