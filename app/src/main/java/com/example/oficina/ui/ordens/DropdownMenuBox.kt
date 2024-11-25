package com.example.oficina.ui.ordens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.oficina.models.Status

// Componente para seleção de status
@Composable
fun DropdownMenuBox(selectedStatus: Status, onStatusSelected: (Status) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Status: ${selectedStatus.name.capitalize()}")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Aberta") },
                onClick = {
                    onStatusSelected(Status.ABERTA)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Finalizada") },
                onClick = {
                    onStatusSelected(Status.FINALIZADA)
                    expanded = false
                }
            )
        }
    }
}