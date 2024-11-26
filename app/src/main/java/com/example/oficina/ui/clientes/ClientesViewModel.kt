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
    private val userId = auth.currentUser?.uid ?: ""
    private val clientesCollection = db.collection("users").document(userId).collection("clientes")
    private val veiculosCollection = db.collection("users").document(userId).collection("veiculos")

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

    init {
        fetchClientes()
    }

    /**
     * Função para buscar todos os clientes uma única vez.
     */
    fun fetchClientes() {
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
     * Utiliza uma query para buscar placas que começam com a string fornecida.
     */
    fun searchVeiculosByPlaca(placa: String) {
        // Inicia o processo de busca
        _isLoading.value = true
        _searchError.value = null

        // Limpa resultados anteriores
        _searchResults.value = emptyList()

        // Realiza a busca no Firestore
        viewModelScope.launch {
            try {
                // Firestore não possui operador "startsWith", mas podemos simular usando whereGreaterThanOrEqualTo e whereLessThanOrEqualTo com \uf8ff
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

    /**
     * Função para adicionar um veículo ao Firestore (se necessário).
     * (Opcional, caso deseje adicionar veículos diretamente através desta ViewModel)
     */
    fun addVeiculo(veiculo: Veiculo, onComplete: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                veiculosCollection.add(veiculo).await()
                onComplete()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}
