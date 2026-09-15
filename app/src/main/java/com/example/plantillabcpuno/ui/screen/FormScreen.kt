package com.example.plantillabcpuno.ui.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    // 1. VALORES POR DEFECTO PARA REUTILIZACIÓN:
    // Permite que la misma pantalla funcione para "Crear" (valores vacíos)
    // o para "Editar" (recibiendo los datos del elemento existente).
    initialTitle: String = "",
    initialSubtitle: String = "",
    isEditing: Boolean = false, // Bandera para alternar títulos y textos dinámicamente

    // 2. STATE HOISTING (Elevación de eventos):
    // La pantalla no modifica bases de datos ni listas directamente;
    // notifica al contenedor padre los datos validados mediante una función lambda.
    onNavigateBack: () -> Unit = {},
    onSubmit: (title: String, subtitle: String) -> Unit = { _, _ -> }
) {
    // 3. ESTADOS MUTABLES LOCALES:
    // 'remember' retiene el texto mientras el usuario escribe en los campos de entrada.
    var title by remember { mutableStateOf(initialTitle) }
    var subtitle by remember { mutableStateOf(initialSubtitle) }

    // 4. VALIDACIÓN REACTIVA EN TIEMPO REAL:
    // Variable booleana derivada que se recalcula en cada pulsación de tecla.
    // Evita enviar datos en blanco o con solo espacios.
    val isFormValid = title.isNotBlank() && subtitle.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                // Título dinámico dependiente del modo de uso
                title = { Text(if (isEditing) "Editar Registro" else "Nuevo Registro") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Respeta la altura del TopAppBar para no solaparse
                .padding(16.dp)
                // 5. SCROLL VERTICAL: Imprescindible para que el teclado virtual
                // no tape los campos inferiores ni el botón en pantallas pequeñas.
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espaciado uniforme entre componentes
        ) {
            // Campo de entrada de línea única
            OutlinedTextField(
                value = title,
                onValueChange = { title = it }, // Actualiza el estado reactivo
                label = { Text("Título *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo de entrada multilínea (área de texto expansible)
            OutlinedTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text("Descripción / Detalle *") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 6. BOTÓN CONTROLADO POR ESTADO (ENABLED / DISABLED):
            // Solo se activa si 'isFormValid' es true, evitando validaciones manuales al pulsar clic.
            Button(
                onClick = { onSubmit(title, subtitle) },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Actualizar" else "Guardar")
            }
        }
    }
}