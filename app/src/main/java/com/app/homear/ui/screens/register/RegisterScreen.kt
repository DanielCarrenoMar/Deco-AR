package com.app.homear.ui.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.homear.ui.component.InputData
import com.app.homear.ui.component.InputPassword
import com.app.homear.ui.component.ButtonSwitch

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel()
)
{
    //esta variable me va a decir si esta activado el apartado de cliente o no
    var isClientActive by remember { mutableStateOf(true) }

    fun crearUsuarioCliente(nombre: String, email: String, password: String)
    {}

    fun crearUsuarioEmpresa(nombre: String, rif: String, password: String)
    {}


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    )
    {
        //titulo de la pantalla
        Text(
            modifier = Modifier.padding(top = 30.dp, bottom = 20.dp),
            text = "Registrarse",
            fontSize = 45.sp,
            fontWeight = FontWeight.W800,
            color = Color("#8F006D".toColorInt()),
        )

        //Boton que cambia los registrar
        ButtonSwitch({isClientActive = !isClientActive}, true, "Cliente", "Empresa")

        Spacer(modifier = Modifier.height(20.dp))

        //condicional que contral que formulario se muestra
        if (isClientActive)
        {
            FormClient(
                {nombre, email, password -> crearUsuarioCliente(nombre, email, password)}
                , {}
            )
        }
        else
        {
            FormEmpresa(
                {nombre, rif, password -> crearUsuarioEmpresa(nombre, rif, password)}
                , {}
            )
        }

    }
}

//Composable para el formulario del Cliente
@Composable
fun FormClient(
    crearUsuario: (nombre: String, email: String, password: String) -> Unit,
    onclickText:()-> Unit
)
{
    //variables para captar datos
    var nombre = remember { mutableStateOf("") }
    var correoElectronico = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var confirmPassword = remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column()
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        )
        {
            //input de Nombre
            InputData(
                dataValue = nombre,
                label = "Nombre Completo",
                placeHolder = "Ingrese su nombre completo"
            )

            //input email
            InputData(
                dataValue = correoElectronico,
                label = "Correo Electrónico",
                placeHolder = "Ingrese su correo Electrónico"
            )

            //Input password
            InputPassword(
                dataValue = password,
                label = "Contraseña",
                placeHolder = "Ingrese su contraseña",
                messageError = "",
                triggerMessageError = false,
                forgotPasswordMessage = "",
                onClickForgotPassword = {}
            )


        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            //Input password confirmar
            InputPassword(
                dataValue = confirmPassword,
                label = "Confirmar contraseña",
                placeHolder = "Ingrese su contraseña",
                messageError = "Contraseña incorrecta",
                triggerMessageError = error,
                forgotPasswordMessage = "",
                onClickForgotPassword = {}
            )

        }


        // Botón de enviar
        TextButton(
            onClick = {

                if (password.value == confirmPassword.value)
                {
                    error = false
                    crearUsuario(nombre.value, correoElectronico.value, password.value)
                    //Limpiar los Input
                    nombre.value = ""
                    correoElectronico.value = ""
                    password.value = ""
                    confirmPassword.value = ""
                }
                else
                {
                    //controla si se muestra el error
                    error = true
                    confirmPassword.value =""
                }


            },
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(horizontal = 15.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color("#8F006D".toColorInt()))
            ,
        )
        {
            Text(
                text = "Crear cuenta",
                style = TextStyle(
                    fontSize = 30.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }


        // texto Adicional
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center
        )
        {
            //texto informativo
            Text(
                text = "¿Ya tienes una cuenta?  ",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color("#807D7D".toColorInt())
            )

            //texto Clickable
            Text(
                text = "Inicia sesión",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color("#8F006D".toColorInt()),
                style = TextStyle(
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .clickable{
                        //aqui se ejecuta la funcion que se pase por parametros
                        onclickText()
                    }
            )
        }
    }

}

//Composable para el formulario de la Empresa
@Composable
fun FormEmpresa(
    crearUsuario: (nombre: String, rif: String, password: String) -> Unit,
    onclickText:()-> Unit
)
{
    //variables para captar datos
    var nombre = remember { mutableStateOf("") }
    var rif = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var confirmPassword = remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column()
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        )
        {
            //input de Nombre
            InputData(
                dataValue = nombre,
                label = "Nombre de la Empresa",
                placeHolder = "Ingrese el nombre completo"
            )

            //input email
            InputData(
                dataValue = rif,
                label = "RIF",
                placeHolder = "Ingrese su RIF"
            )

            //Input password
            InputPassword(
                dataValue = password,
                label = "Contraseña",
                placeHolder = "Ingrese su contraseña",
                messageError = "",
                triggerMessageError = false,
                forgotPasswordMessage = "",
                onClickForgotPassword = {}
            )


        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            //Input password confirmar
            InputPassword(
                dataValue = confirmPassword,
                label = "Confirmar contraseña",
                placeHolder = "Ingrese su contraseña",
                messageError = "Contraseña incorrecta",
                triggerMessageError = error,
                forgotPasswordMessage = "",
                onClickForgotPassword = {}
            )

        }

        // Botón de enviar
        TextButton(
            onClick = {

                if (password.value == confirmPassword.value)
                {
                    error = false
                    crearUsuario(nombre.value, rif.value, password.value)
                    //Limpiar los Input
                    nombre.value = ""
                    rif.value = ""
                    password.value = ""
                    confirmPassword.value = ""
                }
                else
                {
                    //controla si se muestra el error
                    error = true
                    confirmPassword.value =""
                }


            },
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(horizontal = 15.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color("#8F006D".toColorInt()))
            ,
        ) {
            Text(
                text = "Crear cuenta",
                style = TextStyle(
                    fontSize = 30.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }


        // texto Adicional
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center
        )
        {
            //texto informativo
            Text(
                text = "¿Ya tienes una cuenta?  ",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color("#807D7D".toColorInt())
            )

            //texto Clickable
            Text(
                text = "Inicia sesión",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color("#8F006D".toColorInt()),
                style = TextStyle(
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .clickable{
                        //aqui se ejecuta la funcion que se pase por parametros
                        onclickText()
                    }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    RegisterScreen()
}