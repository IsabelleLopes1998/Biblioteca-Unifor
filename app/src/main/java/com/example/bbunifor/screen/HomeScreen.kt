package com.example.bbunifor.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bbunifor.components.AdicionarLembreteDialog
import com.example.bbunifor.components.BookCard
import com.example.bbunifor.components.LembretesListDialog
import com.example.bbunifor.screen.LoginScreen
import com.example.bbunifor.service.NotificationService
import com.example.bbunifor.viewmodel.HomeViewModel
import com.example.bbunifor.viewmodel.LembreteViewModel
import com.example.compose.AppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Composable
fun HomeScreen(innerPadding: PaddingValues,
               navController: NavController,
               homeViewModel: HomeViewModel = viewModel(),
               lembreteViewModel: LembreteViewModel = viewModel()
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var livroSelecionado by remember { mutableStateOf<com.example.bbunifor.model.Book?>(null) }
    var mostrarDialog by remember { mutableStateOf(false) }
    var mostrarLembretesDialog by remember { mutableStateOf(false) }

    val home = homeViewModel.homeState.collectAsState()
    val lembretes = lembreteViewModel.lembretes.collectAsState()
    val isLoadingLembretes = lembreteViewModel.isLoading.collectAsState()

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    lembreteViewModel.carregarLembretes()
                    mostrarLembretesDialog = true
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("📚 Ver Meus Lembretes")
            }
            
            OutlinedButton(
                onClick = {
                    Firebase.auth.signOut()
                    navController.navigate(LoginScreen) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
            ) {
                Text("🚪 Sair")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = home.value.codigoLivro,
                onValueChange = {
                    homeViewModel.updateCodigo(it)
                },
                label = { Text("Código do Livro (ISBN)") },
                modifier = Modifier
                    .weight(1f),
                enabled = !home.value.isLoading
            )

            Button(
                onClick = { 
                    homeViewModel.buscarLivro()
                },
                modifier = Modifier
                    .height(56.dp)
                    .width(100.dp),
                enabled = !home.value.isLoading
            ) { 
                if (home.value.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Buscar") 
                }
            }
        }


        home.value.error?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                textAlign = TextAlign.Center
            )
        }


        if (home.value.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Exibe o livro encontrado
        home.value.livro?.let { livro ->
            Spacer(modifier = Modifier.height(16.dp))
            BookCard(
                book = livro,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onLembrarClick = {
                    livroSelecionado = livro
                    mostrarDialog = true
                }
            )
        }
    }

    // Diálogo para adicionar lembrete
    if (mostrarDialog && livroSelecionado != null) {
        AdicionarLembreteDialog(
            livro = livroSelecionado!!,
            onDismiss = {
                mostrarDialog = false
                livroSelecionado = null
            },
            onConfirm = { dataEntrega ->
                val notificationService = NotificationService(context)
                lembreteViewModel.adicionarLembrete(livroSelecionado!!, dataEntrega, notificationService)
                
                scope.launch {
                    snackbarHostState.showSnackbar("Lembrete adicionado com sucesso!")
                }
                
                mostrarDialog = false
                livroSelecionado = null
            }
        )
    }

    // Diálogo de lembretes
    if (mostrarLembretesDialog) {
        LembretesListDialog(
            lembretes = lembretes.value,
            isLoading = isLoadingLembretes.value,
            onDismiss = {
                mostrarLembretesDialog = false
            },
            onRemoverLembrete = { lembreteId ->
                lembreteViewModel.removerLembrete(lembreteId)
                scope.launch {
                    snackbarHostState.showSnackbar("Lembrete removido")
                }
            }
        )
    }

    // Snackbar para feedback
    SnackbarHost(hostState = snackbarHostState)
}

@Serializable
object HomeScreen

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    AppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            // Preview sem ViewModel - usando estado local
            var codigoLivro by remember { mutableStateOf("") }
            
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 32.dp,
                            start = 16.dp,
                            end = 16.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = codigoLivro,
                        onValueChange = { codigoLivro = it },
                        label = { Text("Código do Livro") },
                        modifier = Modifier
                            .weight(1f)  // Ocupa o espaço disponível
                    )

                    Button(
                        onClick = { },
                        modifier = Modifier
                            .height(56.dp)
                            .width(100.dp)  // Largura fixa para deixar mais quadrado
                    ) { 
                        Text("Buscar") 
                    }
                }
            }
        }
    }
}