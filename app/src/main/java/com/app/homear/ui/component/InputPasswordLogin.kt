package com.app.homear.ui.component

import com.app.homear.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt

// Input para Contraseñas
/**
 * @param dataValue es la variable que recibe la informacion que el usuario escribe
 * @param label es la label del input
 * @param placeHolder es el placeholder del input
 * @param messageError es el texto a mostrar si se activa un error
 * @param triggerMessageError controla si se muestra el mensaje de error o no
 * @param forgotPasswordMessage es el texto para preguntar si se te olvido la password
 * @param onClickForgotPassword es la funcion que se ejecuta si hacer click en el texto forgotPasswordMessage
 */
@Composable
fun InputPassword(
    dataValue: MutableState<String>,
    label: String,
    placeHolder: String,
    messageError: String,
    triggerMessageError: Boolean,
    forgotPasswordMessage: String,
    onClickForgotPassword: () -> Unit,
)
{
    //variables de control del input

    //controlo si se puede ver el texto o no
    var passwordVisible by remember { mutableStateOf(false) }

    // Input Password
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

            // Campo para el correo
            OutlinedTextField(
                value = dataValue.value,  // Accede al valor del estado
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
                shape = RoundedCornerShape(10.dp), //modificar borderRadius del input
                //esto controla si se puede ver la contraseña o no
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                //este es el icono de visibilidad
                trailingIcon = {
                    //aqui se elige que icono mostrar
                    val image = if (passwordVisible) {
                        painterResource(id = R.drawable.icono_visibility) // Ícono de ojo abierto
                    } else {
                        painterResource(id = R.drawable.icon_visibility_off) // Ícono de ojo cerrado
                    }
                    //para que el icono se pueda hacer click
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = image,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = Color.Gray
                        )
                    }
                }
            )


            // Mesajes adicionales

            Box(modifier = Modifier.fillMaxWidth())
            {
                //texto Forgot Password
                Text(
                    text = forgotPasswordMessage,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color("#8F006D".toColorInt()),
                    style = TextStyle(
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable{
                            //aqui se ejecuta la funcion que se pase por parametros
                            onClickForgotPassword()
                        },
                )

                //Texto Error
                //esta variable se pasa por parametros y contrala el mensaje de error
                if(triggerMessageError)
                {
                    Row(
                        modifier = Modifier.align(Alignment.CenterStart).padding(vertical = 10.dp),
                    )
                    {
                        Icon(
                            painter = painterResource(R.drawable.icon_error_fill),
                            contentDescription = "icono Favorito",
                            tint = Color.Red,
                        )
                        Text(
                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 8.dp),
                            text = messageError,
                            style = TextStyle(
                                fontSize = 10.sp
                            ),
                            color = Color.Red
                        )
                    }
                }
            }

        }

    }
}