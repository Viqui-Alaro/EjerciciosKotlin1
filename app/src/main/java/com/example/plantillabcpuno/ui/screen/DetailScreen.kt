package com.example.plantillabcpuno.ui.screen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    // 1. STATE INJECTION (Entrada inmutable):
    // Recibe el objeto con los datos ya resueltos; la vista no consulta repositorios ni base de datos.
    item: ItemUiModel,

    // 2. STATE HOISTING (Manejo de eventos hacia arriba):
    // La pantalla no ejecuta lógica de borrado ni abre pantallas; delega las intenciones mediante callbacks.
    onNavigateBack: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    // 3. CONTROL DE ESTADO DEL DIÁLOGO:
    // Estado booleano local que determina si el diálogo modal de confirmación debe dibujarse o destruirse.
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 4. SCAFFOLD MATERIAL 3:
    // Define la estructura base con TopAppBar que agrupa navegación y acciones contextuales.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Registro") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    // Botón Editar: Notifica a MainActivity para abrir la pantalla de edición
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    // Botón Eliminar: NO borra directo; levanta el diálogo de advertencia
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error // Tinte semántico para acciones destructivas
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Respeta la altura del TopAppBar para no solapar el contenido
                .padding(16.dp)
                // 5. SCROLL VERTICAL: Garantiza accesibilidad en pantallas de baja resolución o texto extenso
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card de cabecera con jerarquía tipográfica (Título prominente e ID como metadata secundaria)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Identificador: ${item.id}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider() // Separador visual Material 3

            Text(
                text = "Descripción / Detalle",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 6. RENDERIZADO CONDICIONAL: Solo reserva espacio y pinta el chip si el badge no es nulo
            item.badge?.let { badgeText ->
                Spacer(modifier = Modifier.height(8.dp))
                AssistChip(
                    onClick = {},
                    label = { Text("Estado: $badgeText") }
                )
            }
        }
    }

    // 7. ALERTDIALOG DECLARATIVO (Patrón de seguridad para acciones destructivas):
    // Se dibuja en la jerarquía solo cuando 'showDeleteDialog' es true.
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false }, // Cierra al pulsar fuera del diálogo
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("¿Eliminar registro?") },
            text = { Text("Esta acción quitará '${item.title}' de la lista de forma permanente.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false // Oculta el diálogo
                        onDeleteClick()          // Ejecuta la eliminación en la fuente de verdad
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}