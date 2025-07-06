package com.app.homear.ui.component

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt

@Composable
/**
 * @param cargarFileName se la pasa la variable donde se va a guardar la URL del archivo seleccionado
 * @param text es el texto que tendra el boton
 * @param filtroArchivo string que indica el tipo de archivo que el filtro del buscador de archivos del
 *                      telefono va a dejar seleccionar, ejemplos, para los glb se usa "*", para las
 *                      imagenes se usa "image"
 */
fun FilePicker(
    cargarFileName: (fileUri: String) -> Unit,
    text: String,
    filtroArchivo: String
)
{
    val context = LocalContext.current
    val selectedFileUri = remember { mutableStateOf<Uri?>(null) }

    // Lanza el selector de archivos
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        selectedFileUri.value = uri
        cargarFileName(uri.toString())
    }

    //Cargar Composables
    Box(
        modifier = Modifier.fillMaxWidth()
    )
    {
        Column(
            modifier = Modifier.align ( Alignment.Center ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        )
        {
            TextButton(
                onClick = {
                    // Lanza el selector para cualquier tipo de archivo, o especifica "image/*" para .jpg o "*/*" para .glb
                    launcher.launch("$filtroArchivo/*")
                },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(35.dp)
                    .padding(0.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color("#8F006D".toColorInt()))
                ,
            ) {
                Text(
                    text = text,
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                )
            }

            // Muestra el URI seleccionado
            selectedFileUri.value?.let { uri ->
                val fileName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    cursor.moveToFirst()
                    cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                } ?: "Archivo sin nombre"

                //cargar composable
                Text(
                    text = "$fileName",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color("#807D7D".toColorInt())
                )
            }

        }

    }

}

@Preview(showBackground = true)
@Composable
fun Preview2() {
    FilePicker(
        {fileUri -> Unit},
        "Añadir Modelo",
        "*"
    )
}