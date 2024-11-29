package com.example.oficina.ui.veiculos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oficina.models.Veiculo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class VeiculosViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid

    private val veiculosCollection = userId?.let {
        db.collection("users").document(it).collection("veiculos")
    }

    private val _veiculos = MutableStateFlow<List<Veiculo>>(emptyList())
    val veiculos: StateFlow<List<Veiculo>> get() = _veiculos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> get() = _authError

    init {
        if (userId == null) {
            _authError.value = "Usuário não autenticado. Faça login para continuar."
        } else {
            fetchVeiculos()
        }
    }


    fun fetchVeiculos() {
        if (veiculosCollection == null) {
            _error.value = "Usuário não autenticado. Não é possível buscar veículos."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                veiculosCollection.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        _error.value = "Erro ao buscar veículos: ${error.message}"
                        return@addSnapshotListener
                    }
                    val veiculosList = snapshot?.toObjects(Veiculo::class.java) ?: emptyList()
                    _veiculos.value = veiculosList
                }
            } catch (e: Exception) {
                _error.value = "Erro ao buscar veículos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun addVeiculo(veiculo: Veiculo, onComplete: () -> Unit, onFailure: (Exception) -> Unit) {
        if (veiculosCollection == null) {
            onFailure(IllegalStateException("Usuário não autenticado. Não é possível adicionar veículos."))
            return
        }

        viewModelScope.launch {
            try {
                veiculosCollection.add(veiculo).await()
                onComplete()
                fetchVeiculos() // Atualiza a lista após adicionar
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }


    fun deleteVeiculo(veiculoId: String, onComplete: () -> Unit, onFailure: (Exception) -> Unit) {
        if (veiculosCollection == null) {
            onFailure(IllegalStateException("Usuário não autenticado. Não é possível excluir veículos."))
            return
        }

        viewModelScope.launch {
            try {
                veiculosCollection.document(veiculoId).delete().await()
                onComplete()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }



    fun updateVeiculo(veiculoId: String, veiculo: Veiculo, onComplete: () -> Unit, onFailure: (Exception) -> Unit) {
        if (veiculosCollection == null) {
            onFailure(IllegalStateException("Usuário não autenticado. Não é possível atualizar veículos."))
            return
        }

        viewModelScope.launch {
            try {
                veiculosCollection.document(veiculoId).set(veiculo).await()
                onComplete()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

}
