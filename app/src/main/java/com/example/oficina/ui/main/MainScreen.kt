package com.example.oficina.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.oficina.models.NavigationItem
import com.example.app.ui.components.BottomNavigationBar

@Composable
fun MainScreen(
    selectedItem: NavigationItem,
    onNavigate: (NavigationItem) -> Unit,
    content: @Composable () -> Unit
) {
    val items = listOf(
        NavigationItem("Ordens de Serviço (OS)", "OS"),
        NavigationItem("Clientes", "Clientes"),
        NavigationItem("Veículos", "Veículos"),
        NavigationItem("Conta", "Conta")
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = items,
                selectedItem = selectedItem,
                onItemSelected = onNavigate
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            content()
        }
    }
}
