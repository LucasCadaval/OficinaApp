package com.example.oficina.ui.main

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.oficina.models.NavigationItem
import com.example.oficina.ui.account.AccountScreen
import com.example.app.ui.components.BottomNavigationBar
import androidx.compose.material3.Text

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val items = listOf(
        NavigationItem("Ordens de Serviço (OS)", "OS"),
        NavigationItem("Clientes", "Clientes"),
        NavigationItem("Veículos", "Veículos"),
        NavigationItem("Conta", "Conta")
    )

    var selectedItem by remember { mutableStateOf(items.first()) }
    val context = LocalContext.current

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
            when (selectedItem.label) {
                "Ordens de Serviço (OS)" -> Text("Tela de Ordens de Serviço")
                "Clientes" -> Text("Tela de Clientes")
                "Veículos" -> Text("Tela de Veículos")
                "Conta" -> AccountScreen(context = context, onLogout = onLogout)
            }
        }
    }
}
