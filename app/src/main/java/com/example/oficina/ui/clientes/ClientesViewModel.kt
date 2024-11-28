package com.example.oficina.ui.clientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oficina.models.Cliente
import com.example.oficina.models.Veiculo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ClientesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid

    private val clientesCollection = userId?.let {
        db.collection("users").document(it).collection("clientes")
    }
    private val veiculosCollection = userId?.let {
        db.collection("users").document(it).collection("veiculos")
    }

    private val _clientes = MutableStateFlow<List<Cliente>>(emptyList())
    val clientes: StateFlow<List<Cliente>> get() = _clientes

    // Estados para gerenciamento de busca
    private val _searchResults = MutableStateFlow<List<Veiculo>>(emptyList())
    val searchResults: StateFlow<List<Veiculo>> get() = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> get() = _searchError

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> get() = _authError

    init {
        if (userId == null) {
            _authError.value = "Usuário não autenticado. Faça login para continuar."
        } else {
            fetchClientes()
        }
    }

    /**
     * Função para buscar todos os clientes uma única vez.
     */
    fun fetchClientes() {
        if (clientesCollection == null) {
            _error.value = "Usuário não autenticado. Não é possível buscar clientes."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val snapshot = clientesCollection.get().await()
                val clientesList = snapshot.documents.mapNotNull { document ->
                    document.toObject(Cliente::class.java)?.copy(id = document.id)
                }
                _clientes.value = clientesList
            } catch (e: Exception) {
                _error.value = "Erro ao buscar clientes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Função para adicionar um novo cliente ao Firestore.
     */
    fun addCliente(cliente: Cliente, onComplete: () -> Unit, onFailure: (Exception) -> Unit) {
        if (clientesCollection == null) {
            onFailure(IllegalStateException("Usuário não autenticado. Não é possível adicionar clientes."))
            return
        }

        viewModelScope.launch {
            try {
                clientesCollection.add(cliente).await()
                onComplete()
                fetchClientes() // Atualiza a lista após adicionar
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    /**
     * Função para deletar um cliente pelo ID.
     */
    fun deleteCliente(clienteId: String, onComplete: () -> Unit, onFailure: (Exception) -> Unit) {
        if (clientesCollection == null) {
            onFailure(IllegalStateException("Usuário não autenticado. Não é possível deletar clientes."))
            return
        }

        viewModelScope.launch {
            try {
                clientesCollection.document(clienteId).delete().await()
                onComplete()
                fetchClientes() // Atualiza a lista após deletar
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    /**
     * Função para atualizar um cliente existente.
     */
    fun updateCliente(clienteId: String, cliente: Cliente, onComplete: () -> Unit, onFailure: (Exception) -> Unit) {
        if (clientesCollection == null) {
            onFailure(IllegalStateException("Usuário não autenticado. Não é possível atualizar clientes."))
            return
        }

        viewModelScope.launch {
            try {
                clientesCollection.document(clienteId).set(cliente).await()
                onComplete()
                fetchClientes() // Atualiza a lista após atualizar
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    /**
     * Função para buscar veículos no Firestore com base na placa fornecida.
     */
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

    /**
     * Função para limpar os resultados da busca.
     */
    fun clearSearchResults() {
        _searchResults.value = emptyList()
        _searchError.value = null
    }
}
