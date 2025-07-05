package com.app.homear.ui.screens.addProducto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.app.homear.ui.component.FilePicker
import com.app.homear.ui.component.InputData

@Composable
fun AddProductoScreen()
{
    //variables para captar datos
    // las varibles fileUri pueden ser null sino se elige archivos
    var fileUriModelo by remember { mutableStateOf("Hola") }
    var fileUriImage by remember { mutableStateOf("") }
    var description = remember { mutableStateOf("") }
    var alto = remember { mutableStateOf("") }
    var ancho = remember { mutableStateOf("") }
    var profundidad = remember { mutableStateOf("") }
    var material = remember { mutableStateOf("") }

    //variables para controlar el scroll
    val scrollState = rememberScrollState()

    //declaracion de funciones de uso

    //funcion al presionar el boton cancelar
    fun onClickCancel(){}

    //funcion para agregar producto
    fun AddProducto(fileUrlModelo: String, fileUrlImage: String,
                    description: String, alto: String, ancho: String,
                    profundidad: String, material: String)
    {}

    //funcion al presionar el boton confirmar
    fun onClickConfirm()
    {
        //Limpiar los Input
        description.value = ""
        alto.value = ""
        ancho.value = ""
        profundidad.value = ""
        material.value = ""
    }



    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    )
    {
        //titulo de la pantalla
        Text(
            modifier = Modifier.padding(top = 30.dp),
            text = "Añadir producto",
            fontSize = 40.sp,
            fontWeight = FontWeight.W800,
            color = Color("#8F006D".toColorInt()),
        )

        //spacer
        Spacer(modifier = Modifier.height(40.dp))

        //botones para cargar archivos

        //carga el archivo modelo
        FilePicker(
            {fileUri -> fileUriModelo = fileUri},
            "Añadir Modelo",
            "*"
        )

        //spacer
        Spacer(modifier = Modifier.height(35.dp))

        //carga el archivo modelo
        FilePicker(
            {fileUri -> fileUriImage = fileUri},
            "Añadir Imagen",
            "image"
        )

        //spacer
        Spacer(modifier = Modifier.height(35.dp))

        //cargar Inputs

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        )
        {
            //input de Descripcion
            InputData(
                dataValue = description,
                label = "Descripción",
                placeHolder = "Añadir descripción del producto"
            )

            //input de altura
            InputData(
                dataValue = alto,
                label = "Alto",
                placeHolder = "Añadir el alto del producto (cm)"
            )

            //input de anchura
            InputData(
                dataValue = ancho,
                label = "Ancho",
                placeHolder = "Añadir el ancho del producto (cm)"
            )

            //input de Profundidad
            InputData(
                dataValue = profundidad,
                label = "Profundidad",
                placeHolder = "Añadir la profundidad del producto (cm)"
            )

            //input de Material
            InputData(
                dataValue = material,
                label = "Material",
                placeHolder = "Añadir el material del producto"
            )
            //spacer
            Spacer(modifier = Modifier.height(10.dp))

            //botones para cancelar o confirmar
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.SpaceAround
            )
            {
                // Botón para Cancelar
                TextButton(
                    onClick = { onClickCancel() },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                        .width(150.dp)
                        .height(40.dp)
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
                    onClick = {onClickConfirm()},
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color("#8F006D".toColorInt()))
                        .width(150.dp)
                        .height(40.dp)
                ) {
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

//composable para preview de la pantalla
@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    AddProductoScreen()
}