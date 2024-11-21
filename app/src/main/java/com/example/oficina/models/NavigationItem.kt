package com.example.oficina.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(val label: String, val route: String, val icon: ImageVector = Icons.Default.Home)
