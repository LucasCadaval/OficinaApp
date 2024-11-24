package com.example.oficina.models

data class OrdemServico(
    val id: String = "", // ID gerado pelo Firestore
    val clienteId: String = "", // Referência ao ID do cliente
    val clienteNome: String = "", // Nome do cliente para exibição
    val problema: String = "",
    val pecas: List<Peca> = emptyList(),
    val valorTotal: Double = 150.0 // Valor inicial da mão de obra
)
