package com.example.oficina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.oficina.data.AppDatabase
import com.example.oficina.models.Cliente
import com.example.oficina.models.NavigationItem
import com.example.oficina.models.Veiculo
import com.example.oficina.ui.account.ContaScreen
import com.example.oficina.ui.clientes.ClienteDetailsScreen
import com.example.oficina.ui.clientes.ClientesScreen
import com.example.oficina.ui.clientes.ClientesViewModel
import com.example.oficina.ui.clientes.NovoClienteScreen
import com.example.oficina.ui.login.LoginScreen
import com.example.oficina.ui.main.MainScreen
import com.example.oficina.ui.veiculos.NovoVeiculoScreen
import com.example.oficina.ui.veiculos.VeiculosScreen
import com.example.oficina.ui.veiculos.VeiculosViewModel
import com.example.oficina.ui.veiculos.VeiculoDetailScreen
import com.example.oficina.ui.ordens.OrdemServicoScreen
import com.example.oficina.ui.ordens.OrdemServicoViewModel
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = applicationContext
            var isLoggedIn by remember { mutableStateOf(false) }
            var selectedItem by remember { mutableStateOf<NavigationItem?>(null) }
            var currentScreen by remember { mutableStateOf("home") }
            var selectedCliente by remember { mutableStateOf<Cliente?>(null) }
            var selectedVeiculo by remember { mutableStateOf<Veiculo?>(null) }

            // ViewModels
            val ordemViewModel: OrdemServicoViewModel = viewModel()
            val clientesViewModel: ClientesViewModel = viewModel()
            val veiculosViewModel: VeiculosViewModel = viewModel()

            // Verifica se o usuário está logado ao iniciar
            LaunchedEffect(Unit) {
                val user = AppDatabase.getDatabase(context).userDao().getLoggedInUser()
                isLoggedIn = user != null
                if (isLoggedIn) {
                    // Define o item selecionado padrão após o login
                    selectedItem = NavigationItem("OS", "OS", Icons.Default.Home)
                    currentScreen = "OS"
                }
            }

            if (isLoggedIn) {
                selectedItem?.let { selected ->
                    MainScreen(
                        selectedItem = selected,
                        onNavigate = { item ->
                            selectedItem = item
                            currentScreen = item.label
                            // Reseta estados de seleção ao mudar de tela
                            selectedCliente = null
                            selectedVeiculo = null
                        },
                        content = {
                            when (currentScreen) {
                                "OS" -> {
                                    OrdemServicoScreen(
                                        viewModel = ordemViewModel,
                                        onBack = { currentScreen = "home" }
                                    )
                                }
                                "Clientes" -> {
                                    ClientesScreen(
                                        viewModel = clientesViewModel,
                                        onAddCliente = { currentScreen = "novo_cliente" },
                                        onClienteClick = { cliente ->
                                            selectedCliente = cliente
                                            currentScreen = "cliente_details"
                                        }
                                    )
                                }
                                "cliente_details" -> {
                                    selectedCliente?.let { cliente ->
                                        ClienteDetailsScreen(
                                            cliente = cliente,
                                            onBack = { currentScreen = "Clientes" }
                                        )
                                    }
                                }
                                "novo_cliente" -> {
                                    NovoClienteScreen(
                                        viewModel = clientesViewModel,
                                        onBack = { currentScreen = "Clientes" }
                                    )
                                }
                                "Veículos" -> {
                                    VeiculosScreen(
                                        viewModel = veiculosViewModel,
                                        onAddVeiculo = { currentScreen = "novo_veiculo" },
                                        onVeiculoClick = { veiculo ->
                                            selectedVeiculo = veiculo
                                            currentScreen = "veiculo_details"
                                        }
                                    )
                                }
                                "veiculo_details" -> {
                                    selectedVeiculo?.let { veiculo ->
                                        VeiculoDetailScreen(
                                            veiculo = veiculo,
                                            onBack = { currentScreen = "Veículos" }
                                        )
                                    }
                                }
                                "novo_veiculo" -> {
                                    NovoVeiculoScreen(
                                        viewModel = veiculosViewModel,
                                        onBack = { currentScreen = "Veículos" }
                                    )
                                }
                                "Conta" -> {
                                    ContaScreen(
                                        context = context,
                                        onLogout = {
                                            isLoggedIn = false
                                            selectedItem = null
                                            currentScreen = "login"
                                        }
                                    )
                                }
                                "home" -> {
                                    Text("Tela Inicial")
                                }
                                else -> {
                                    Text("Tela não encontrada")
                                }
                            }
                        }
                    )
                }
            } else {
                // Tela de login
                LoginScreen(onLoginSuccess = {
                    isLoggedIn = true
                    selectedItem = NavigationItem("OS", "OS", Icons.Default.List)
                    currentScreen = "OS"
                })
            }
        }
    }
}
