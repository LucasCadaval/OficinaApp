package com.example.oficina.ui.clientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oficina.models.Cliente
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClientesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _clientes = MutableStateFlow<List<Cliente>>(emptyList())
    val clientes: StateFlow<List<Cliente>> get() = _clientes

    init {
        fetchClientes()
    }

    private fun fetchClientes() {
        db.collection("clientes").addSnapshotListener { value, _ ->
            val clientes = value?.toObjects(Cliente::class.java) ?: emptyList()
            _clientes.value = clientes
        }
    }

    fun addCliente(cliente: Cliente, onComplete: () -> Unit) {
        db.collection("clientes").add(cliente).addOnSuccessListener { onComplete() }
    }
}
