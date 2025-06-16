package com.app.homear.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
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
import com.app.homear.ui.component.InputPassword
import com.app.homear.ui.component.InputData

@Composable
fun LoginScreen()
{
    // Estados para los campos del formulario
    var email = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .background(Color("#006B4F".toColorInt()))
        .fillMaxSize().padding(0.dp))
    {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
            ,
            shape = RoundedCornerShape(topStart = 125.dp)
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.80f)
                    .background(Color("#EAE1DF".toColorInt()))
                ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            )
            {
                //titulo de la pantalla
                Text(
                    modifier = Modifier.padding(top = 80.dp, bottom = 20.dp),
                    text = "Iniciar Sesion",
                    fontSize = 45.sp,
                    fontWeight = FontWeight.W800,
                    color = Color("#8F006D".toColorInt()),
                )

                //contenedor para los Inputs
                Column(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                )
                {
                    // Input Normal
                    InputData(
                        dataValue =  email, // Sincroniza el estado con la var
                        label =  "Correo electronico",
                        placeHolder =  "ingrese su correo electronico"
                    )

                    //input Password
                    InputPassword(
                        dataValue =  password, // Sincroniza el estado con la var
                        label =  "Contraseña",
                        placeHolder =  "ingrese su contraseña",
                        messageError = "contraseña incorrecta",
                        triggerMessageError = error,
                        forgotPasswordMessage = "¿has olvidad tu contraseña?",
                        onClickForgotPassword = { Unit }
                    )



                    // Botón de enviar
                    TextButton(
                        onClick = {

                            //si se quieren poner validaciones
                            //controla si se muestra el error
                            //error = !error

                            //email.value tiene los valores que ingreso el usuario para el ViewModel
                            //Limpiar los Input
                            email.value= ""
                            password.value = ""

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .padding(top = 25.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color("#8F006D".toColorInt()))
                        ,
                    ) {
                        Text(
                            text = "Iniciar Sesion",
                            style = TextStyle(
                                fontSize = 30.sp,
                                color = Color("#FFFFFF".toColorInt()),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                }

            }

        }


    }
}

@Preview(showBackground = true)
@Composable
fun FormScreenPreview() {
    LoginScreen()
}