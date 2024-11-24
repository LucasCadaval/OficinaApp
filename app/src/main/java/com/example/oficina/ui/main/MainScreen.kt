package com.example.oficina.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
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
        NavigationItem("OS", "OS", Icons.Default.List),
        NavigationItem("Clientes", "Clientes", Icons.Default.Person),
        NavigationItem("Veículos", "Veículos", Icons.Default.Build),
        NavigationItem("Conta", "Conta", Icons.Default.AccountCircle)
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
