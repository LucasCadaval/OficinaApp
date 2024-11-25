package com.example.oficina.ui.ordens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oficina.models.Cliente

@Composable
fun ClienteItem(
    cliente: Cliente,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = cliente.nome, style = MaterialTheme.typography.bodyLarge)
            Text(text = "CPF: ${cliente.cpf}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Endereço: ${cliente.endereco}, ${cliente.cidade}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
