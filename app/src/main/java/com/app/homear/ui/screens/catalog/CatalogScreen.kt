package com.app.homear.ui.screens.catalog

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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.rememberImagePainter
import java.io.File
import java.io.FileOutputStream

fun saveFileToMedia(context: Context, fileName: String, fileContent: ByteArray): String? {
    val mediaDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.parentFile?.parentFile
        ?.resolve("media")

    if (mediaDir != null && !mediaDir.exists()) {
        mediaDir.mkdirs() // Crea la carpeta si no existe
    }

    return try {
        val file = File(mediaDir, fileName)
        FileOutputStream(file).use { output ->
            output.write(fileContent)
        }
        file.absolutePath // Retorna la ruta del archivo guardado
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getMediaFiles(context: Context): List<File> {
    val mediaDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.parentFile?.parentFile
        ?.resolve("media")

    return mediaDir?.listFiles()?.filter { it.isFile } ?: emptyList()
}

@Composable
fun CatalogScreen(
    navigateToHome: () -> Unit,
    viewModel: CatalogViewModel = hiltViewModel(),
) {
    val showDialog = remember { mutableStateOf(false) }
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
                putExtra(
                    Intent.EXTRA_MIME_TYPES,
                    arrayOf("model/gltf-binary", "application/octet-stream")
                )
            }
            filePickerLauncher.launch(intent)
        }) {
            Text(text = "Importar archivo .glb")
        }


        // EL localContext es parA localizar todo lo referente a las rutas del proyecto actual
        selectedFileUri?.let { uri ->
            val context = LocalContext.current
            // En caso de tener problemas para obtener el nombre con la URI
            val fileName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    cursor.getString(nameIndex)
                } else {
                    "archivo_seleccionado.glb"
                }
            } ?: "archivo_seleccionado.glb"
            val inputStream = context.contentResolver.openInputStream(uri)
            val filePath = inputStream?.use { stream ->
                saveFileToMedia(context, fileName, stream.readBytes())
            } ?: "Error: No se pudo leer el archivo"

            Text(text = "Archivo guardado en: $filePath")
        }

        val context = LocalContext.current
        val mediaFiles = remember { getMediaFiles(context) }


        // Layout para mostrar el grid con la vista previa de los modelos (aún no he puesto la vista previa ya que no hemos definido donde se van a guardar los modelos)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2 Columnas
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 116.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Por los momentos solo cargo 6 elementos
            items(mediaFiles.size) { index ->
                val file = mediaFiles[index]
                Box(
                    modifier = Modifier
                        .background(color = Color(0xFFefefef))
                        .fillMaxSize()
                        .height(100.dp)
                        .width(100.dp)
                        .clickable(
                            enabled = true
                        ) {
                            showDialog.value = true
                        }
                ) {
                    val painter =
                        rememberImagePainter(data = "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEhMeyVirGc0WA_ev1-BX5W9DtsSrHsaaXCe2SmUcPI-N7soXsxZZv8-n1n18Uhjz_syxbkpB-lM9hslx8-8ts-cRyPJAl1kDPwe7mMfdvkz8abyL6iJeF18pV6t6rp7vexPy_Z4/s1600/imagenes-gratis-para-ver-y-compartir-en-facebook-y-google+-fotos-free-photos-to-share+(1).jpg")
                    Image(
                        painter = painter,
                        contentDescription = "Imagen cargada de internet",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f),
        verticalArrangement = Arrangement.Bottom
    ) {
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

    if (showDialog.value) {
        Dialog(onDismissRequest = { showDialog.value = false }) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    text = "¿Quieres descargar este recurso?",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                ) {
                    Button(modifier = Modifier.padding(end = 32.dp),
                        onClick = {
                        }) {
                        Text(text = "Descargar")
                    }
                    Button(onClick = { showDialog.value = false }) {
                        Text(text = "Cerrar")
                    }
                }
            }
        }
    }

}

// Para previsualizar el frontend
@Preview(showBackground = true)
@Composable
fun PreviewCatalogScreen() {
    CatalogScreen(navigateToHome = { /* Acción de navegación */ })
}