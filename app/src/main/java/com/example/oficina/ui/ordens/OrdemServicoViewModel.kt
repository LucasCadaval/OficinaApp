package com.example.oficina.ui.ordens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oficina.models.Cliente
import com.example.oficina.models.OrdemServico
import com.example.oficina.models.Status
import com.example.oficina.models.Veiculo
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
    private val userId = auth.currentUser?.uid

    private val ordensCollection = userId?.let {
        db.collection("users").document(it).collection("ordens_servico")
    }

    private val veiculosCollection = userId?.let {
        db.collection("users").document(it).collection("veiculos")
    }

    // Estado das ordens de serviço
    private val _ordens = MutableStateFlow<List<OrdemServico>>(emptyList())
    val ordens: StateFlow<List<OrdemServico>> get() = _ordens

    // Estado para controle de filtro
    private val _filtro = MutableStateFlow(FiltroOrdenServico.ABERTAS)
    val filtro: StateFlow<FiltroOrdenServico> get() = _filtro

    // Estados para clientes
    private val _searchClientesResults = MutableStateFlow<List<Cliente>>(emptyList())
    val searchClientesResults: StateFlow<List<Cliente>> get() = _searchClientesResults

    private val _isLoadingClientes = MutableStateFlow(false)
    val isLoadingClientes: StateFlow<Boolean> get() = _isLoadingClientes

    private val _clientesError = MutableStateFlow<String?>(null)
    val clientesError: StateFlow<String?> get() = _clientesError

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> get() = _authError

    // Estados para gerenciamento de busca
    private val _searchResults = MutableStateFlow<List<Veiculo>>(emptyList())
    val searchResults: StateFlow<List<Veiculo>> get() = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> get() = _searchError

    init {
        if (userId == null) {
            _authError.value = "Usuário não autenticado. Faça login para continuar."
        } else {
            fetchOrdens()
        }
    }

    private fun fetchOrdens() {
        ordensCollection?.let { collection ->
            viewModelScope.launch {
                val query: Query = when (_filtro.value) {
                    FiltroOrdenServico.TODAS -> collection
                    FiltroOrdenServico.ABERTAS -> collection.whereEqualTo("status", Status.ABERTA.name)
                    FiltroOrdenServico.FINALIZADAS -> collection.whereEqualTo("status", Status.FINALIZADA.name)
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
        } ?: run {
            _authError.value = "Usuário não autenticado. Não é possível buscar ordens de serviço."
        }
    }

    fun setFiltro(filtro: FiltroOrdenServico) {
        _filtro.value = filtro
        fetchOrdens() // Re-fetch com o novo filtro
    }

    fun saveOrdemServico(ordem: OrdemServico, onComplete: () -> Unit, onFailure: (Exception) -> Unit) {
        if (ordensCollection == null) {
            onFailure(IllegalStateException("Usuário não autenticado. Não é possível salvar ordens de serviço."))
            return
        }

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

    fun excluirOrdemServico(ordemId: String, onComplete: () -> Unit, onFailure: (Exception) -> Unit) {
        if (ordensCollection == null) {
            onFailure(IllegalStateException("Usuário não autenticado. Não é possível excluir ordens de serviço."))
            return
        }

        viewModelScope.launch {
            try {
                ordensCollection.document(ordemId).delete().await()
                onComplete()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun searchClientesByNome(nome: String) {
        if (userId == null) {
            _clientesError.value = "Usuário não autenticado. Não é possível buscar clientes."
            return
        }

        _isLoadingClientes.value = true
        _clientesError.value = null
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection("users").document(userId).collection("clientes")
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

    fun searchVeiculosByPlaca(placa: String) {
        if (veiculosCollection == null) {
            _searchError.value = "Usuário não autenticado. Não é possível buscar veículos."
            return
        }

        _isLoading.value = true
        _searchError.value = null
        _searchResults.value = emptyList()

        viewModelScope.launch {
            try {
                val querySnapshot = veiculosCollection
                    .whereGreaterThanOrEqualTo("placa", placa)
                    .whereLessThanOrEqualTo("placa", placa + "\uf8ff")
                    .get()
                    .await()

                val resultados = querySnapshot.toObjects(Veiculo::class.java)
                _searchResults.value = resultados
            } catch (e: Exception) {
                _searchError.value = "Erro ao buscar veículos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
        _searchError.value = null
    }

    fun clearSearchClientes() {
        _searchClientesResults.value = emptyList()
        _clientesError.value = null
    }
}
