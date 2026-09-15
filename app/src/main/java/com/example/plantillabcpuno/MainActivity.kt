package com.example.plantillabcpuno

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.plantillabcp.ui.screen.ApiScreen
import com.example.plantillabcpuno.ui.screen.DetailScreen
import com.example.plantillabcpuno.ui.screen.FormScreen

import com.example.plantillabcpuno.ui.screen.GenericListScreen
import com.example.plantillabcpuno.ui.screen.ItemUiModel
import com.example.plantillabcpuno.ui.theme.PlantillaBCPUnoTheme

// 1. ENUM TYPE-SAFE: Define las rutas sin usar Strings "hardcodeados",
// evitando errores tipográficos en tiempo de compilación.
// Añadimos EDIT a la lista de pantallas posibles
enum class AppScreen { API, LIST, FORM, DETAIL, EDIT }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentScreen by remember { mutableStateOf(AppScreen.LIST) }

            val itemsList = remember {
                mutableStateListOf(
                    ItemUiModel("1", "Elemento A", "Subtítulo A", "Activo"),
                    ItemUiModel("2", "Elemento B", "Subtítulo B", null)
                )
            }

            var selectedItemId by remember { mutableStateOf<String?>(null) }
            val currentSelectedItem = itemsList.find { it.id == selectedItemId }

            when (currentScreen) {
                // PANTALLA API: Permite ver el servicio y regresar a la lista local
                AppScreen.API -> ApiScreen(
                    onNavigateBack = { currentScreen = AppScreen.LIST }
                )

                // PANTALLA LISTA LOCAL: Contiene el botón "Ver API"
                AppScreen.LIST -> GenericListScreen(
                    items = itemsList,
                    onAddNewClick = { currentScreen = AppScreen.FORM },
                    onItemClick = { clickedId ->
                        selectedItemId = clickedId
                        currentScreen = AppScreen.DETAIL
                    },
                    onNavigateToApi = {
                        // Cambia la vista activa a la pantalla del servicio REST
                        currentScreen = AppScreen.API
                    }
                )

                // PANTALLA CREAR
                AppScreen.FORM -> FormScreen(
                    isEditing = false,
                    onNavigateBack = { currentScreen = AppScreen.LIST },
                    onSubmit = { title, subtitle ->
                        val newId = System.currentTimeMillis().toString()
                        itemsList.add(
                            ItemUiModel(
                                id = newId,
                                title = title,
                                subtitle = subtitle,
                                badge = "Nuevo"
                            )
                        )
                        currentScreen = AppScreen.LIST
                    }
                )

                // PANTALLA DETALLE
                AppScreen.DETAIL -> {
                    if (currentSelectedItem != null) {
                        DetailScreen(
                            item = currentSelectedItem,
                            onNavigateBack = { currentScreen = AppScreen.LIST },
                            onEditClick = { currentScreen = AppScreen.EDIT },
                            onDeleteClick = {
                                itemsList.removeAll { it.id == currentSelectedItem.id }
                                currentScreen = AppScreen.LIST
                            }
                        )
                    } else {
                        currentScreen = AppScreen.LIST
                    }
                }

                // PANTALLA EDITAR
                AppScreen.EDIT -> {
                    if (currentSelectedItem != null) {
                        FormScreen(
                            initialTitle = currentSelectedItem.title,
                            initialSubtitle = currentSelectedItem.subtitle,
                            isEditing = true,
                            onNavigateBack = { currentScreen = AppScreen.DETAIL },
                            onSubmit = { updatedTitle, updatedSubtitle ->
                                val index = itemsList.indexOfFirst { it.id == currentSelectedItem.id }
                                if (index != -1) {
                                    itemsList[index] = currentSelectedItem.copy(
                                        title = updatedTitle,
                                        subtitle = updatedSubtitle
                                    )
                                }
                                currentScreen = AppScreen.LIST
                            }
                        )
                    } else {
                        currentScreen = AppScreen.LIST
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PlantillaBCPUnoTheme {
        Greeting("Android")
    }
}