package com.example.oficina.models

data class OrdemServico(
    val id: String = "", // Este campo será preenchido com o ID do documento no Firestore
    val clienteId: String = "",
    val clienteNome: String = "",
    val problema: String = "",
    val pecas: List<Peca> = emptyList(),
    val valorTotal: Double = 0.0,
    val status: Status = Status.ABERTA,
    val veiculos: List<String> = emptyList()
)
