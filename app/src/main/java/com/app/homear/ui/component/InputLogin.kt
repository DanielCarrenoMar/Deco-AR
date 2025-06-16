package com.app.homear.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt

// Input Normal
/**
 * @param dataValue es la variable que recibe la informacion que el usuario escribe
 * @param label es la label del input
 * @param placeHolder es el placeholder del input
 */
@Composable
fun InputData(
    dataValue: MutableState<String>,
    label: String,
    placeHolder: String
)
{

    Box()
    {
        Column()
        {
            //label
            Text(
                text = label,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color("#656565".toColorInt()),
            )
            Spacer(modifier = Modifier.height(10.dp))

            //este es el input
            OutlinedTextField(
                value = dataValue.value, // Accede al valor del estado
                onValueChange = {newValue ->
                    dataValue.value = newValue // Actualiza el estado
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {Text( //este es el placeHolder y sus estilos personalizados
                    text = placeHolder,
                    style = TextStyle(
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                )},
                textStyle = TextStyle( // este es el estilo de la letras cuando se escribe en el input
                    color = Color("#656565".toColorInt()),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                colors = OutlinedTextFieldDefaults.colors //estilos para personalizar el input
                    (
                    focusedContainerColor = Color.LightGray,
                    unfocusedContainerColor = Color.LightGray,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
                ,
                shape = RoundedCornerShape(10.dp) //modificar borderRadius del input
            )
        }

    }
}