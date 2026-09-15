package com.example.plantillabcpuno.ui.screen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// 1. MODELO INMUTABLE PARA LA UI: Representa un item desacoplado de la base de datos o red.
// El badge es nullable (String?) para soportar elementos sin etiqueta.
data class ItemUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericListScreen(
    // 2. PARÁMETROS STATELESS (Sin estado interno rígido):
    // La pantalla recibe datos inmutables y emite eventos hacia arriba mediante lambdas (State Hoisting).
    items: List<ItemUiModel> = emptyList(),
    onItemClick: (String) -> Unit = {},
    onAddNewClick: () -> Unit = {},
    onNavigateToApi: () -> Unit = {} // Callback para delegar la navegación a MainActivity
) {
    // 3. ESTADO DE BÚSQUEDA: Mantiene el texto del buscador durante recomposiciones simples.
    var searchQuery by remember { mutableStateOf("") }

    // 4. OPTIMIZACIÓN DE RENDIMIENTO CON REMEMBER (Keys):
    // Evita recalcular el filtro en cada frame; solo se reejecuta si cambian 'searchQuery' o 'items'.
    val filteredItems = remember(searchQuery, items) {
        if (searchQuery.isBlank()) items
        else items.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.subtitle.contains(searchQuery, ignoreCase = true)
        }
    }

    // 5. SCAFFOLD MATERIAL 3: Estructura base que gestiona barras, FABs y áreas de contenido.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registros Locales") },
                actions = {
                    // Botón para saltar al flujo del consumo API REST
                    FilledTonalButton(
                        onClick = onNavigateToApi,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ver API")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNewClick) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo")
            }
        }
    ) { innerPadding ->
        // innerPadding: Evita que el contenido quede solapado debajo del TopAppBar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Buscador reactivo
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            // 6. MANEJO DE ESTADO VACÍO (Empty State):
            // Si la búsqueda no coincide con nada, muestra un mensaje descriptivo en lugar de una pantalla en blanco.
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron resultados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                // 7. LAZYCOLUMN (Equivalente al RecyclerView clásico):
                // Recicla y dibuja solo los elementos visibles en pantalla para optimizar memoria.
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // key = { it.id }: Identificador único esencial para que Compose optimice
                    // animaciones, reordenamientos y eliminaciones sin redibujar toda la lista.
                    items(filteredItems, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onItemClick(item.id) }, // Propaga el ID seleccionado
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.title, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        item.subtitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                // Renderizado condicional del Badge si no es nulo
                                item.badge?.let {
                                    Badge { Text(it) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}