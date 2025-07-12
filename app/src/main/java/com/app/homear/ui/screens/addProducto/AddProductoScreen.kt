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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
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
import com.app.homear.domain.model.UserModel
import com.app.homear.ui.component.FilePicker
import com.app.homear.ui.component.InputData
import android.app.Application
import androidx.compose.ui.platform.LocalContext
import com.app.homear.core.ModelJsonManager
import org.json.JSONObject
import androidx.compose.foundation.clickable

@Composable
fun AddProductoScreen(
    onCancel: () -> Unit,
    onSuccess: () -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToSpacesList: () -> Unit,
    viewModel: AddProductoViewModel = hiltViewModel(),
    user: UserModel // Corrección del parámetro y tipo para proveer email
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
    // Nueva variable para superficie
    val superficieOptions = listOf("Piso", "Techo", "Pared")
    var superficie = remember { mutableStateOf("") } // Por defecto vacío

    // Variables para controlar el scroll
    val scrollState = rememberScrollState()

    // Estado del diálogo
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    // Estado para error de superficie no seleccionada
    var showSuperficieError by remember { mutableStateOf(false) }

    // Para obtener contexto de la aplicación
    val context = LocalContext.current.applicationContext

    // Al lanzar la pantalla (o en el primer uso), asegúrate de copiar el modelo si no existe
    LaunchedEffect(Unit) {
        ModelJsonManager.copyModelJsonIfNeeded(context)
    }

    // Observar cambios en el estado
    LaunchedEffect(viewModel.state.isSuccess) {
        if (viewModel.state.isSuccess) {
            // --- NUEVO: Agregar al model.json local ---
            val productJson = JSONObject().apply {
                put("modelUri", fileUriModelo)
                put("imageUri", fileUriImage)
                put("name", name.value)
                put("description", description.value)
                put("height", alto.value)
                put("width", ancho.value)
                put("length", profundidad.value)
                put("materials", material.value)
                put("superficie", when (superficie.value.uppercase()) {
                    "PISO" -> "PISO"
                    "TECHO" -> "TECHO"
                    "PARED" -> "PARED"
                    else -> "PISO"
                })
                put("createdAt", System.currentTimeMillis()) // campo opcional de timestamp
                put("proveedorEmail", user.email)
            }
            ModelJsonManager.addFurniture(context, productJson)
            // ---
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
        showSuperficieError = false
        if (fileUriModelo.isNotEmpty() && fileUriImage.isNotEmpty() &&
            name.value.isNotEmpty() && description.value.isNotEmpty() &&
            alto.value.isNotEmpty() && ancho.value.isNotEmpty() &&
            profundidad.value.isNotEmpty() && material.value.isNotEmpty() &&
            superficie.value.isNotEmpty()
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
                superficie = when (superficie.value) {
                    "Piso" -> Superficie.PISO
                    "Techo" -> Superficie.TECHO
                    "Pared" -> Superficie.PARED
                    else -> Superficie.PISO // fallback
                },
                proveedorEmail = user.email
            )
        } else if (superficie.value.isEmpty()) {
            showSuperficieError = true
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

                // Selector de Material visualmente igual a InputData
                val materiales = listOf("Madera", "Metal", "Plástico", "Vidrio", "Piedra", "Otro")
                var expanded by remember { mutableStateOf(false) }
                var expandedSuperficie by remember { mutableStateOf(false) }

                Column {
                    // Nuevo selector de Superficie (antes que Material)
                    Text(
                        text = "Superficie",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color("#656565".toColorInt()),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box {
                        OutlinedTextField(
                            value = superficie.value,
                            onValueChange = {}, // No editable manualmente
                            modifier = Modifier
                                .fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "Selecciona una superficie",
                                    style = TextStyle(
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                )
                            },
                            textStyle = TextStyle(
                                color = Color("#656565".toColorInt()),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.LightGray,
                                unfocusedContainerColor = Color.LightGray,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(10.dp),
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "Desplegar",
                                    tint = Color.Gray,
                                    modifier = Modifier.clickable(enabled = !viewModel.state.isLoading) { expandedSuperficie = true }
                                )
                            },
                            singleLine = true,
                            enabled = !viewModel.state.isLoading,
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        )
                        // Box transparente encima para capturar el click en todo el campo
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable(
                                    enabled = !viewModel.state.isLoading,
                                    indication = null, // Sin efecto visual extra
                                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                                ) { expandedSuperficie = true }
                        )
                    }
                    DropdownMenu(
                        expanded = expandedSuperficie,
                        onDismissRequest = { expandedSuperficie = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        superficieOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    superficie.value = option
                                    expandedSuperficie = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selector de Material visualmente igual a InputData
                    Text(
                        text = "Material",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color("#656565".toColorInt()),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box {
                        OutlinedTextField(
                            value = material.value,
                            onValueChange = {}, // No editable manualmente
                            modifier = Modifier
                                .fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "Selecciona un material",
                                    style = TextStyle(
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                )
                            },
                            textStyle = TextStyle(
                                color = Color("#656565".toColorInt()),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.LightGray,
                                unfocusedContainerColor = Color.LightGray,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(10.dp),
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "Desplegar",
                                    tint = Color.Gray,
                                    modifier = Modifier.clickable(enabled = !viewModel.state.isLoading) { expanded = true }
                                )
                            },
                            singleLine = true,
                            enabled = !viewModel.state.isLoading,
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        )
                        // Box transparente encima para capturar el click en todo el campo
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable(
                                    enabled = !viewModel.state.isLoading,
                                    indication = null, // Sin efecto visual extra
                                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                                ) { expanded = true }
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        materiales.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    material.value = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

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
                        enabled = !viewModel.state.isLoading && superficie.value.isNotEmpty()
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
                superficie.value = "" // Resetear superficie
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
                        superficie.value = "" // Resetear superficie
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

    // Mensaje de error para superficie no seleccionada
    if (showSuperficieError) {
        Text(
            text = "Debes seleccionar una superficie",
            color = Color.Red,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 8.dp)
        )
    }
}