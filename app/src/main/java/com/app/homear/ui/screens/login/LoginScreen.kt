package com.app.homear.ui.screens.login

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    navigateToTutorial: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var showLogin by remember { mutableStateOf(true) }
    val isUserLoggedIn: Boolean by viewModel.isLogged.observeAsState(false)

    viewModel.isLogeed()

    BackHandler(enabled = isUserLoggedIn) {
        navigateToTutorial()
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!isUserLoggedIn) {
            if (showLogin) {
                LoginCase(
                    onLoginSuccess = {
                        showLogin = false
                        navigateToTutorial()
                    },
                    onNavigateToRegister = { showLogin = false },
                    viewModel = viewModel
                )
            } else {
                RegisterCase(
                    onRegisterSuccess = {
                        navigateToTutorial()
                    },
                    onNavigateToLogin = { showLogin = true },
                    viewModel = viewModel

                )
            }
        } else {
            Text("Iniciando sesión...")
            LaunchedEffect(Unit) {
                navigateToTutorial()
            }
        }
    }
}

@Composable
fun LoginCase(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit, viewModel: LoginViewModel) {
    val email : String by viewModel.email.observeAsState("")
    val password :String by viewModel.pass.observeAsState("")
    val showDialog: Boolean by viewModel.error.observeAsState(false)

    var isAdmin by remember { mutableStateOf(false) }
    var adminCodeVisible by remember { mutableStateOf(false) }
    var adminCode by remember { mutableStateOf("") }
    val loginError: Boolean by viewModel.error.observeAsState(false)

    var passwordVisible by remember { mutableStateOf(false) }


    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Iniciar Sesión")

            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onLoginChange(it, password) },
                label = { Text("Correo Electrónico") },
                leadingIcon = { Icon(Icons.Filled.MailOutline, contentDescription = "Correo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.onLoginChange(email, it) },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Contraseña") },
                trailingIcon = {
                    val imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Hide password" else "Show password"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = imageVector, contentDescription = description)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = isAdmin,
                    onCheckedChange = {
                        isAdmin = it
                        adminCodeVisible = it
                    }
                )
                Text("Administrador")
            }

            AnimatedVisibility(
                visible = adminCodeVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                OutlinedTextField(
                    value = adminCode,
                    onValueChange = { adminCode = it },
                    label = { Text("Código de Administrador") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Código Admin") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    viewModel.loginUser(email,password,onLoginSuccess)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("Iniciar Sesión", color = Color.White, fontSize = 18.sp)
            }

            TextButton(onClick = onNavigateToRegister) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.onChangeError(false) },
                    title = { Text("Error de Inicio de Sesión") },
                    //text = { Text(loginError) },
                    confirmButton = {
                        Button(onClick = {viewModel.onChangeError(false)}) {
                            Text("Aceptar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun RegisterCase(onRegisterSuccess: () -> Unit, onNavigateToLogin: () -> Unit, viewModel: LoginViewModel) {
    val email: String by viewModel.email.observeAsState("")
    val password : String by viewModel.pass.observeAsState("")
    val showDialog : Boolean by viewModel.error.observeAsState(false)
    var isAdmin by remember { mutableStateOf(false) }
    var adminCodeVisible by remember { mutableStateOf(false) }
    var adminCode by remember { mutableStateOf("") }
    var registerError by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }

    /*val usersFileName = "users.json"
    val adminSecretCode = "ADMIN123"
    val context = LocalContext.current

    fun getUsersJson(): JSONObject? {
        val file = File(context.filesDir, usersFileName)
        return try {
            if (file.exists()) {
                val jsonString = file.readText()
                JSONObject(jsonString)
            } else {
                JSONObject()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun isUserAlreadyRegistered(email: String, isAdmin: Boolean): Boolean {
        val usersJson = getUsersJson() ?: return false
        val userType = if (isAdmin) "admins" else "clients"

        if (usersJson?.has(userType) == true) {
            val users = usersJson.getJSONObject(userType)
            return users.has(email)
        }
        return false
    }

    fun saveUserToJson(email: String, password: String, isAdmin: Boolean, adminCode: String) {
        val usersJson = getUsersJson() ?: JSONObject()
        val userType = if (isAdmin) "admins" else "clients"

        if (usersJson?.has(userType) != true) {
            usersJson.put(userType, JSONObject())
        }

        val users = usersJson.getJSONObject(userType)
        val newUser = JSONObject()
        newUser.put("password", password)
        newUser.put("isAdmin", isAdmin)
        if (isAdmin) {
            newUser.put("adminCode", adminCode)
        }
        users.put(email, newUser)

        val file = File(context.filesDir, usersFileName)
        try {
            file.writeText(usersJson.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            registerError = "Error al guardar la información."
            showDialog = true
        }
    }*/

    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Registrarse")

            OutlinedTextField(
                value = email,
                onValueChange = {viewModel.onLoginChange(it, password) },
                label = { Text("Correo Electrónico") },
                leadingIcon = { Icon(Icons.Filled.MailOutline, contentDescription = "Correo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.onLoginChange(email, it) },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Contraseña") },
                trailingIcon = {
                    val imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Hide password" else "Show password"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = imageVector, contentDescription = description)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = isAdmin,
                    onCheckedChange = {
                        isAdmin = it
                        adminCodeVisible = it
                    }
                )
                Text("Registrar como Administrador")
            }

            AnimatedVisibility(
                visible = adminCodeVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                OutlinedTextField(
                    value = adminCode,
                    onValueChange = { adminCode = it },
                    label = { Text("Código de Administrador") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Código Admin") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    viewModel.registerUser(email,password,onRegisterSuccess)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("Registrarse", color = Color.White, fontSize = 18.sp)
            }

            TextButton(onClick = onNavigateToLogin) {
                Text("¿Ya tienes cuenta? Inicia sesión aquí")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.onChangeError(false) },
                    title = { Text("Error de Registro") },
                    text = { Text(registerError) },
                    confirmButton = {
                        Button(onClick = { viewModel.onChangeError(false) }) {
                            Text("Aceptar")
                        }
                    }
                )
            }
        }
    }
}