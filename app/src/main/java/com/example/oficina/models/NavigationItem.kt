package com.example.oficina.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

val navigationItems = listOf(
    NavigationItem("OS", "OS", Icons.Default.List),
    NavigationItem("Clientes", "Clientes", Icons.Default.Person),
    NavigationItem("Veículos", "Veículos", Icons.Default.Build),
    NavigationItem("Conta", "Conta", Icons.Default.AccountCircle)
    // Adicione outros itens conforme necessário
)
