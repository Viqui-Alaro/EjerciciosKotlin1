package com.example.plantillabcp.ui.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.plantillabcp.ui.state.ApiUiState
import com.example.plantillabcp.ui.viewmodel.ApiViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiScreen(
    // 1. INYECCIÓN DEL VIEWMODEL Y DESACOPLAMIENTO:
    // viewModel() obtiene o retiene la instancia del ciclo de vida.
    // onNavigateBack desacopla la pantalla del enrutador raíz (MainActivity).
    viewModel: ApiViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    // 2. OBSERVACIÓN REACTIVA DE FLUJOS ASÍNCRONOS:
    // collectAsState() suscribe la vista al StateFlow del ViewModel.
    // Transforma cada emisión en un State de Compose que gatilla recomposición automática.
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Consumo API REST") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { innerPadding ->
        // 3. CONTENEDOR BASE BOX:
        // Permite centrar loaders o errores de forma absoluta usando Alignment.Center.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Respeta la altura del TopAppBar para no solapar vistas
        ) {
            // 4. MÁQUINA DE ESTADOS FINITOS (Pattern Matching con Sealed Class/Interface):
            // Smart cast automático: según el subtipo de ApiUiState, el compilador expone
            // solo las variables pertinentes (message en Error, posts en Success).
            when (val currentState = state) {
                // ESTADO 1: CARGA (Feedback visual asíncrono)
                is ApiUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                // ESTADO 2: ERROR (Manejo de excepciones de red/HTTP con reintento)
                is ApiUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentState.message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Dispara una nueva corrutina en el ViewModel para reintentar la llamada Retrofit
                        Button(onClick = { viewModel.loadPosts() }) {
                            Text("Reintentar")
                        }
                    }
                }

                // ESTADO 3: ÉXITO (Pintado reactivo de la colección recibida)
                is ApiUiState.Success -> {
                    // LazyColumn recicla componentes en memoria, renderizando solo lo visible
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Tip para entrevista: aquí es buena práctica añadir key = { post.id }
                        // para optimizar el diffing y rendimiento de la lista.
                        items(currentState.posts) { post ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = post.title,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = post.body,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}