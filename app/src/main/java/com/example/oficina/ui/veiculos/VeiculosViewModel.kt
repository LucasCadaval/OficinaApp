package com.example.oficina.ui.veiculos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oficina.models.Veiculo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VeiculosViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _veiculos = MutableStateFlow<List<Veiculo>>(emptyList())
    val veiculos: StateFlow<List<Veiculo>> get() = _veiculos

    init {
        fetchVeiculos()
    }

    private fun fetchVeiculos() {
        db.collection("veiculos").addSnapshotListener { value, _ ->
            val veiculos = value?.toObjects(Veiculo::class.java) ?: emptyList()
            _veiculos.value = veiculos
        }
    }

    fun addVeiculo(veiculo: Veiculo, onComplete: () -> Unit) {
        db.collection("veiculos").add(veiculo).addOnSuccessListener { onComplete() }
    }
}
