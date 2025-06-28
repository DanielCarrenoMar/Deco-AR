package com.app.homear.ui.screens.start

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.app.homear.ui.screens.loading.LoadingViewModel

val CustomPurple = Color(0xFF54124E)
val CustomGreen = Color(0xFF00664B)

@Composable
fun StartScreen(
    viewModel: LoadingViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Canvas con arco verde
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.TopCenter)
        ) {
            val width = size.width
            val height = size.height

            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(0f, height * 1.8f)
                quadraticBezierTo(
                    width * 0.5f,
                    height * 2.2f,
                    width,
                    height * 1.8f
                )
                lineTo(width, 0f)
                close()
            }
            drawPath(
                path = path,
                color = CustomGreen,
                style = Fill
            )
        }

        // Título decorAR en blanco arriba del arco
        Text(
            text = "decorAR",
            color = Color.White,
            fontSize = 60.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp)
        )

        // Imagen centrada encima del arco
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/inicio/chair-start.svg") // coloca aquí tu URL o recurso local
                .decoderFactory(SvgDecoder.Factory())
                .crossfade(true)
                .build(),
            contentDescription = "Silla decorAR",
            modifier = Modifier
                .size(620.dp) // ajusta según tamaño de tu recurso
                .align(Alignment.TopCenter)
                .padding(top = 200.dp) // se alinea para que "flote" encima del arco
        )

        // Botones en la parte inferior
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNavigateToLogin,
                colors = ButtonDefaults.buttonColors(containerColor = CustomPurple),
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.8f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onNavigateToRegister ,
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.8f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = CustomPurple
                )
            ) {
                Text(
                    text = "Registrarse",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
