package com.example.oficina.utils

fun applyCpfMask(cpf: String): String {
    return cpf.filter { it.isDigit() }
        .replaceFirst(Regex("(\\d{3})(\\d{3})(\\d{3})(\\d{2})"), "$1.$2.$3-$4")
}

fun applyCepMask(cep: String): String {
    return cep.filter { it.isDigit() }
        .replaceFirst(Regex("(\\d{5})(\\d{3})"), "$1-$2")
}

fun applyPlacaMask(placa: String): String {
    return placa.uppercase()
        .replaceFirst(Regex("([A-Z]{3})(\\d{4})"), "$1-$2")
}
