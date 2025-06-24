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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.homear.ui.component.InputPassword
import com.app.homear.ui.component.InputData
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    // Estados locales para los inputs
    val localEmail = remember { mutableStateOf("") }
    val localPassword = remember { mutableStateOf("") }
    
    // Observar estados del ViewModel
    val error by viewModel.error.observeAsState(false)
    val isLogged by viewModel.isLogged.observeAsState(false)

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
                        dataValue = localEmail,
                        label = "Correo electronico",
                        placeHolder = "Ingrese su correo electronico"
                    )

                    //input Password
                    InputPassword(
                        dataValue = localPassword,
                        label = "Contraseña",
                        placeHolder = "Ingrese su contraseña",
                        messageError = "Contraseña incorrecta",
                        triggerMessageError = error,
                        forgotPasswordMessage = "¿Olvidó su contraseña?",
                        onClickForgotPassword = { 
                            viewModel.onChangeError(false)
                        }
                    )

                    // Botón de enviar
                    TextButton(
                        onClick = {
                            println("Login attempt with email: ${localEmail.value}, password: ${localPassword.value}")
                            viewModel.loginUser(
                                email = localEmail.value,
                                pass = localPassword.value,
                                onLoginSuccess = onLoginSuccess
                            )
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
