package com.example.oficina.ui.ordens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oficina.models.Cliente
import com.example.oficina.models.OrdemServico
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrdemServicoViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val ordensCollection = db.collection("ordens_servico")

    // Estado das ordens de serviço
    private val _ordens = MutableStateFlow<List<OrdemServico>>(emptyList())
    val ordens: StateFlow<List<OrdemServico>> get() = _ordens

    // Outros estados para clientes
    private val _searchClientesResults = MutableStateFlow<List<Cliente>>(emptyList())
    val searchClientesResults: StateFlow<List<Cliente>> get() = _searchClientesResults

    private val _isLoadingClientes = MutableStateFlow(false)
    val isLoadingClientes: StateFlow<Boolean> get() = _isLoadingClientes

    private val _clientesError = MutableStateFlow<String?>(null)
    val clientesError: StateFlow<String?> get() = _clientesError

    init {
        fetchOrdens()
    }

    private fun fetchOrdens() {
        ordensCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Trate o erro conforme necessário
                return@addSnapshotListener
            }
            val ordensList = snapshot?.toObjects(OrdemServico::class.java) ?: emptyList()
            _ordens.value = ordensList
        }
    }

    fun saveOrdemServico(ordem: OrdemServico, onComplete: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                if (ordem.id.isEmpty()) {
                    // Adicionar nova ordem
                    ordensCollection.add(ordem).await()
                } else {
                    // Atualizar ordem existente
                    ordensCollection.document(ordem.id).set(ordem).await()
                }
                onComplete()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun searchClientesByNome(nome: String) {
        _isLoadingClientes.value = true
        _clientesError.value = null
        viewModelScope.launch {
            try {
                val querySnapshot: QuerySnapshot = FirebaseFirestore.getInstance()
                    .collection("clientes")
                    .whereGreaterThanOrEqualTo("nome", nome)
                    .whereLessThanOrEqualTo("nome", nome + "\uf8ff")
                    .get()
                    .await()
                val resultados = querySnapshot.toObjects(Cliente::class.java)
                _searchClientesResults.value = resultados
            } catch (e: Exception) {
                _clientesError.value = "Erro ao buscar clientes: ${e.message}"
            } finally {
                _isLoadingClientes.value = false
            }
        }
    }

    fun clearSearchClientes() {
        _searchClientesResults.value = emptyList()
        _clientesError.value = null
    }
}
