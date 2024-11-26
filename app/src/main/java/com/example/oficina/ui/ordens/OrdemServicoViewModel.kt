package com.example.oficina.ui.ordens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oficina.models.Cliente
import com.example.oficina.models.OrdemServico
import com.example.oficina.models.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class FiltroOrdenServico {
    TODAS,
    ABERTAS,
    FINALIZADAS
}

class OrdemServicoViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid ?: ""
    private val ordensCollection = db.collection("users").document(userId).collection("ordens_servico")

    // Estado das ordens de serviço
    private val _ordens = MutableStateFlow<List<OrdemServico>>(emptyList())
    val ordens: StateFlow<List<OrdemServico>> get() = _ordens

    // Estado para controle de filtro
    private val _filtro = MutableStateFlow(FiltroOrdenServico.TODAS)
    val filtro: StateFlow<FiltroOrdenServico> get() = _filtro

    // Estados para clientes
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
        viewModelScope.launch {
            val query: Query = when (_filtro.value) {
                FiltroOrdenServico.TODAS -> ordensCollection
                FiltroOrdenServico.ABERTAS -> ordensCollection.whereEqualTo("status", Status.ABERTA.name)
                FiltroOrdenServico.FINALIZADAS -> ordensCollection.whereEqualTo("status", Status.FINALIZADA.name)
            }

            query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Trate o erro conforme necessário (ex: emitir um estado de erro)
                    return@addSnapshotListener
                }
                val ordensList = snapshot?.documents?.map { document ->
                    document.toObject(OrdemServico::class.java)?.copy(id = document.id) ?: OrdemServico()
                } ?: emptyList()
                _ordens.value = ordensList
            }
        }
    }

    fun setFiltro(filtro: FiltroOrdenServico) {
        _filtro.value = filtro
        fetchOrdens() // Re-fetch com o novo filtro
    }

    fun saveOrdemServico(ordem: OrdemServico, onComplete: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                if (ordem.id.isEmpty()) {
                    // Adicionar nova ordem
                    val docRef = ordensCollection.add(ordem).await()
                    // Firestore gera um ID automaticamente
                    // Como estamos usando SnapshotListener, a nova ordem será capturada automaticamente com o ID
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

    fun excluirOrdemServico(ordemId: String, onComplete: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                ordensCollection.document(ordemId).delete().await()
                onComplete()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    // Funções para buscar clientes
    fun searchClientesByNome(nome: String) {
        _isLoadingClientes.value = true
        _clientesError.value = null
        viewModelScope.launch {
            try {
                val querySnapshot = FirebaseFirestore.getInstance()
                    .collection("users").document(userId).collection("clientes")
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
