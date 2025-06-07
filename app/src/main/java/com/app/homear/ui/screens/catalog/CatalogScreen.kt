package com.app.homear.ui.screens.catalog

import android.content.Context
import android.os.Environment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.homear.ui.component.NavBard
import java.io.File
import java.io.FileOutputStream

/**
 * Guarda un archivo en la carpeta de medios de la aplicación.
 */
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

@Composable
fun CatalogScreen(
    navigateToTutorial: () -> Unit,
    navigateToCamera: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToConfiguration: () -> Unit,
    viewModel: CatalogViewModel = hiltViewModel(),
) {

    Column(
        modifier = Modifier.fillMaxSize().zIndex(1f),
        verticalArrangement = Arrangement.Bottom
    ) {
        NavBard(
            toCamera = navigateToCamera,
            toTutorial = navigateToTutorial,
            toCatalog = null,
            toProfile = navigateToProfile,
            toConfiguration = navigateToConfiguration,
        )
    }

}