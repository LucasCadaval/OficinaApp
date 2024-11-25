package com.example.oficina.ui.ordens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FiltroOrdenServico(
    filtroAtual: FiltroOrdenServico,
    onFiltroSelecionado: (FiltroOrdenServico) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        // Botão para "Todas"
        OutlinedButton(
            onClick = { onFiltroSelecionado(FiltroOrdenServico.TODAS) },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (filtroAtual == FiltroOrdenServico.TODAS) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
                contentColor = if (filtroAtual == FiltroOrdenServico.TODAS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .weight(1f)
        ) {
            Text("Todas")
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Botão para "Abertas"
        OutlinedButton(
            onClick = { onFiltroSelecionado(FiltroOrdenServico.ABERTAS) },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (filtroAtual == FiltroOrdenServico.ABERTAS) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
                contentColor = if (filtroAtual == FiltroOrdenServico.ABERTAS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .weight(1f)
        ) {
            Text("Abertas")
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Botão para "Finalizadas"
        OutlinedButton(
            onClick = { onFiltroSelecionado(FiltroOrdenServico.FINALIZADAS) },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (filtroAtual == FiltroOrdenServico.FINALIZADAS) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
                contentColor = if (filtroAtual == FiltroOrdenServico.FINALIZADAS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .weight(1f)
        ) {
            Text("Finalizadas")
        }
    }
}
