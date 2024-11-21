package com.example.app.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.oficina.models.NavigationItem

@Composable
fun BottomNavigationBar(
    items: List<NavigationItem>,
    selectedItem: NavigationItem,
    onItemSelected: (NavigationItem) -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item == selectedItem,
                onClick = { onItemSelected(item) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
