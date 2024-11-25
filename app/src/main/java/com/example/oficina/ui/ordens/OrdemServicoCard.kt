package com.example.oficina.ui.ordens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oficina.models.OrdemServico
import com.example.oficina.models.Status

@Composable
fun OrdemServicoCard(
    ordem: OrdemServico,
    onEdit: () -> Unit,
    onDelete: (String) -> Unit
) {
    val corStatus = when (ordem.status) {
        Status.ABERTA -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        Status.FINALIZADA -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onEdit() },
        colors = CardDefaults.cardColors(containerColor = corStatus),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cliente: ${ordem.clienteNome}",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { onDelete(ordem.id) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir Ordem"
                    )
                }
            }
            Text(
                text = "Problema: ${ordem.problema}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Valor Total: R$${String.format("%.2f", ordem.valorTotal)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Peças:", style = MaterialTheme.typography.bodyMedium)
            ordem.pecas.forEach { peca ->
                Text(
                    text = "- ${peca.nome}: R$${String.format("%.2f", peca.valor)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Status: ${ordem.status.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = when (ordem.status) {
                    Status.ABERTA -> MaterialTheme.colorScheme.error
                    Status.FINALIZADA -> MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}
