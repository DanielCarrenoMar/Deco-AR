package com.app.homear.ui.catalog

import  androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.app.homear.ui.component.NavBard
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import java.io.File
import java.io.FileOutputStream

fun saveFileToMedia(context: Context, fileName: String, fileContent: ByteArray): String? {
    val mediaDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

    try {
        val file = File(mediaDir, fileName)
        FileOutputStream(file).use { output ->
            output.write(fileContent)
        }
        return file.absolutePath
    } catch (e: Exception) {
        Log.e("FileSave", "Error saving file: ${e.message}")
    }
    return null
}

@Composable
fun CatalogScreen (
    navigateToHome: () -> Unit,
    viewModel: CatalogViewModel = hiltViewModel(),
){
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getAllModelFiles(context)
        }
    }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    // Configurar el launcher para seleccionar archivos
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedFileUri = result.data?.data // Obtener la URI del archivo seleccionado
        }
    }

    // Estilos de botón
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Botón para abrir el selector de archivos
        Button(onClick = {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("model/gltf-binary", "application/octet-stream"))
            }
            filePickerLauncher.launch(intent)
        }) {
            Text(text = "Importar archivo .glb")
        }


        // EL localContext es parA localizar todo lo referente a las rutas del proyecto actual
        selectedFileUri?.let { uri ->
            val context = LocalContext.current
            val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: "archivo_seleccionado.glb"
            val inputStream = context.contentResolver.openInputStream(uri)
            val filePath = inputStream?.use { stream ->
                saveFileToMedia(context, fileName, stream.readBytes())
            } ?: "Error: No se pudo leer el archivo"

            Text(text = "Archivo guardado en: $filePath")
        }



        // Layout para mostrar el grid con la vista previa de los modelos (aún no he puesto la vista previa ya que no hemos definido donde se van a guardar los modelos)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2 Columnas
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Por los momentos solo cargo 6 elementos
            items(6) { index ->
                Box(
                    modifier = Modifier
                        .background(color = Color(0xFFefefef))
                        .fillMaxSize()
                        .height(100.dp)
                        .width(100.dp)
                ) {
                    Text(
                        text = "Item ${index + 1}", // Texto que se muestra en cada elemento
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    Column (
        modifier = Modifier.fillMaxSize().zIndex(1f),
        verticalArrangement = Arrangement.Bottom
    ){
        NavBard(
            items = listOf(
                NavBard.NavBarItem(
                    title = "Home",
                    icon = -1,
                    onClick = navigateToHome
                ),
                NavBard.NavBarItem(
                    title = "Catalogo",
                    icon = -1,
                    onClick = null
                )
            )
        )
    }
}

// Para previsualizar el frontend
@Preview(showBackground = true)
@Composable
fun PreviewCatalogScreen() {
    CatalogScreen(navigateToHome = { /* Acción de navegación */ })
}