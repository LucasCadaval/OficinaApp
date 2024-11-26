package com.example.oficina.models

data class Cliente(
    val id: String = "",
    val nome: String = "",
    val cpf: String = "",
    val cep: String = "",
    val endereco: String = "",
    val cidade: String = "",
    val contato: String = "",
    val veiculos: List<String> = emptyList() //
)



