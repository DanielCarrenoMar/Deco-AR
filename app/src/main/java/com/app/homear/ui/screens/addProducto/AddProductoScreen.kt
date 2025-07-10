package com.app.homear.ui.screens.addProducto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.homear.domain.model.Superficie
import com.app.homear.ui.component.FilePicker
import com.app.homear.ui.component.InputData

@Composable
fun AddProductoScreen(
    onCancel: () -> Unit,
    onSuccess: () -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToSpacesList: () -> Unit,
    viewModel: AddProductoViewModel = hiltViewModel(),
) {
    // Variables para captar datos
    var fileUriModelo by remember { mutableStateOf("") }
    var fileUriImage by remember { mutableStateOf("") }
    var name = remember { mutableStateOf("") }
    var description = remember { mutableStateOf("") }
    var alto = remember { mutableStateOf("") }
    var ancho = remember { mutableStateOf("") }
    var profundidad = remember { mutableStateOf("") }
    var material = remember { mutableStateOf("") }

    // Variables para controlar el scroll
    val scrollState = rememberScrollState()

    // Estado del diálogo
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // Observar cambios en el estado
    LaunchedEffect(viewModel.state.isSuccess) {
        if (viewModel.state.isSuccess) {
            showSuccessDialog = true
        }
    }

    LaunchedEffect(viewModel.state.errorMessage) {
        if (viewModel.state.errorMessage != null) {
            showErrorDialog = true
        }
    }

    // Función al presionar el botón cancelar
    fun onClickCancel() {
        onCancel()
    }

    // Función al presionar el botón confirmar
    fun onClickConfirm() {
        if (fileUriModelo.isNotEmpty() && fileUriImage.isNotEmpty() &&
            name.value.isNotEmpty() && description.value.isNotEmpty() &&
            alto.value.isNotEmpty() && ancho.value.isNotEmpty() &&
            profundidad.value.isNotEmpty() && material.value.isNotEmpty()
        ) {

            viewModel.addProduct(
                modelUri = fileUriModelo,
                imageUri = fileUriImage,
                name = name.value,
                description = description.value,
                height = alto.value,
                width = ancho.value,
                length = profundidad.value,
                materials = material.value,
                superficie = Superficie.PISO // Por defecto, podrías agregar un selector
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Título de la pantalla
            Text(
                modifier = Modifier.padding(top = 30.dp),
                text = "Añadir producto",
                fontSize = 40.sp,
                fontWeight = FontWeight.W800,
                color = Color("#8F006D".toColorInt()),
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Botones para cargar archivos
            FilePicker(
                { fileUri -> fileUriModelo = fileUri },
                "Añadir Modelo",
                "*"
            )

            Spacer(modifier = Modifier.height(35.dp))

            FilePicker(
                { fileUri -> fileUriImage = fileUri },
                "Añadir Imagen",
                "image"
            )

            Spacer(modifier = Modifier.height(35.dp))

            // Input para nombre del producto
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Input de nombre
                InputData(
                    dataValue = name,
                    label = "Nombre del producto",
                    placeHolder = "Ingrese el nombre del producto"
                )

                // Input de Descripción
                InputData(
                    dataValue = description,
                    label = "Descripción",
                    placeHolder = "Añadir descripción del producto"
                )

                // Input de altura
                InputData(
                    dataValue = alto,
                    label = "Alto (metros)",
                    placeHolder = "Añadir el alto del producto"
                )

                // Input de anchura
                InputData(
                    dataValue = ancho,
                    label = "Ancho (metros)",
                    placeHolder = "Añadir el ancho del producto"
                )

                // Input de Profundidad
                InputData(
                    dataValue = profundidad,
                    label = "Profundidad (metros)",
                    placeHolder = "Añadir la profundidad del producto"
                )

                // Input de Material
                InputData(
                    dataValue = material,
                    label = "Material",
                    placeHolder = "Añadir el material del producto (separar con comas)"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Botones para cancelar o confirmar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Botón para Cancelar
                    TextButton(
                        onClick = { onClickCancel() },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                            .width(150.dp)
                            .height(40.dp),
                        enabled = !viewModel.state.isLoading
                    ) {
                        Text(
                            text = "Cancelar",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        )
                    }

                    // Botón para Confirmar
                    TextButton(
                        onClick = { onClickConfirm() },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color("#8F006D".toColorInt()))
                            .width(150.dp)
                            .height(40.dp),
                        enabled = !viewModel.state.isLoading
                    ) {
                        if (viewModel.state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Confirmar",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Indicador de carga global
        if (viewModel.state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = Color("#8F006D".toColorInt())
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Subiendo producto...",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

    // Diálogo de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                viewModel.clearState()
                // Limpiar campos
                fileUriModelo = ""
                fileUriImage = ""
                name.value = ""
                description.value = ""
                alto.value = ""
                ancho.value = ""
                profundidad.value = ""
                material.value = ""
                onSuccess()
            },
            title = { Text("¡Éxito!") },
            text = { Text("El producto se ha agregado correctamente al catálogo.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.clearState()
                        // Limpiar campos
                        fileUriModelo = ""
                        fileUriImage = ""
                        name.value = ""
                        description.value = ""
                        alto.value = ""
                        ancho.value = ""
                        profundidad.value = ""
                        material.value = ""
                        onSuccess()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Diálogo de error
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                viewModel.clearError()
            },
            title = { Text("Error") },
            text = { Text(viewModel.state.errorMessage ?: "Error desconocido") },
            confirmButton = {
                Button(
                    onClick = {
                        showErrorDialog = false
                        viewModel.clearError()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}

