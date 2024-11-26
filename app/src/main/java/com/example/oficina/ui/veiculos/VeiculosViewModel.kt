package com.example.oficina.ui.veiculos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oficina.models.Cliente
import com.example.oficina.models.Veiculo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class VeiculosViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _veiculos = MutableStateFlow<List<Veiculo>>(emptyList())
    private val veiculosCollection = db.collection("veiculos")
    val veiculos: StateFlow<List<Veiculo>> get() = _veiculos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> get() = _searchError

    init {
        fetchVeiculos()
    }

//    private fun fetchVeiculos() {
//        db.collection("veiculos").addSnapshotListener { value, _ ->
//            val veiculos = value?.toObjects(Veiculo::class.java) ?: emptyList()
//            _veiculos.value = veiculos
//        }
//    }

    fun fetchVeiculos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                db.collection("veiculos").addSnapshotListener { value, _ ->
            val veiculos = value?.toObjects(Veiculo::class.java) ?: emptyList()
            _veiculos.value = veiculos
        }
            } catch (e: Exception) {
                _error.value = "Erro ao buscar veiculos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addVeiculo(veiculo: Veiculo, onComplete: () -> Unit) {
        db.collection("veiculos").add(veiculo).addOnSuccessListener { onComplete() }
    }

    fun deleteVeiculo(veiculoId: String, onComplete: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                veiculosCollection.document(veiculoId).delete().await()
                onComplete()
                fetchVeiculos()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun updateVeiculo(veiculoId: String, veiculo: Veiculo, onComplete: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                veiculosCollection.document(veiculoId).set(veiculo).await()
                onComplete()
                fetchVeiculos()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}
