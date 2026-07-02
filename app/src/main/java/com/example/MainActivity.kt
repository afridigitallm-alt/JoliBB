package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.JoliBBDatabase
import com.example.data.JoliBBRepository
import com.example.ui.*
import com.example.ui.components.JoliBBFooter
import com.example.ui.components.JoliBBHeader
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Local SQLite Room Database & Repository initialization
        val database = JoliBBDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = JoliBBRepository(database.productDao(), database.cartItemDao())
        
        // Setup ViewModel with our constructor injection Factory
        val viewModel: JoliBBViewModel by viewModels {
            JoliBBViewModelFactory(repository)
        }

        setContent {
            MyApplicationTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: JoliBBViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    Scaffold(
        topBar = {
            JoliBBHeader(viewModel = viewModel)
        },
        containerColor = com.example.ui.theme.WarmCream
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Accueil -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        HomeScreen(viewModel = viewModel, modifier = Modifier.weight(1f, fill = false))
                        JoliBBFooter(viewModel = viewModel)
                    }
                }
                is Screen.Catalogue -> {
                    CatalogScreen(viewModel = viewModel)
                }
                is Screen.ProduitDetail -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        DetailScreen(
                            productId = screen.productId,
                            viewModel = viewModel,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        JoliBBFooter(viewModel = viewModel)
                    }
                }
                is Screen.Panier -> {
                    CartScreen(viewModel = viewModel)
                }
                is Screen.AdminLogin, is Screen.AdminDashboard -> {
                    AdminScreen(viewModel = viewModel)
                }
            }
        }
    }
}
