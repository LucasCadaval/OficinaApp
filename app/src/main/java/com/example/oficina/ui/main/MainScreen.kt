package com.example.oficina.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.app.ui.components.BottomNavigationBar
import com.example.oficina.models.NavigationItem
import androidx.compose.material3.Text

@Composable
fun MainScreen() {
    val items = listOf(
        NavigationItem("Ordens de Serviço (OS)", "OS"),
        NavigationItem("Clientes", "Clientes"),
        NavigationItem("Veículos", "Veículos"),
        NavigationItem("Funcionários", "Funcionários")
    )

    var selectedItem by remember { mutableStateOf(items.first()) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = items,
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Tela atual: ${selectedItem.label}")
        }
    }
}
