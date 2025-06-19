package com.app.homear.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.app.homear.ui.theme.Purple60
import com.app.homear.ui.component.NavBard

@Composable
fun HomeScreen(
    onHowItWorksClick: () -> Unit = {},
    navigateToCatalog: (() -> Unit)? = null,
    navigateToCamera: (() -> Unit)? = null,
    navigateToProfile: (() -> Unit)? = null,
    navigateToConfiguration: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Círculo superior derecho (más hacia la derecha)
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/inicio/background.svg")
                .decoderFactory(SvgDecoder.Factory())
                .build(),
            contentDescription = "Círculo decorativo",
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .align(Alignment.TopCenter)
                .offset(x = 60.dp)
        )
        // Rectángulo superior izquierdo (ahora ocupa todo el ancho)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 56.dp)
        ) {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("file:///android_asset/inicio/rectangle.svg")
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentDescription = "Rectángulo decorativo",
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(64.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "decorAR",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                    )
                }
            }
        }
        // Planta y mueble en el centro, mueble ligeramente más grande
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("file:///android_asset/inicio/plant-background.svg")
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = "Planta",
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterStart)
                )
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("file:///android_asset/inicio/furniture-background.svg")
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = "Mueble",
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.CenterEnd)
                )
        }
        // Textos descriptivos y botón
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
                .fillMaxWidth()
                .padding(top = 100.dp), // Espacio vertical respecto al bloque central
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Diseña tu espacio en segundos",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Solo necesitas tu cámara para comenzar a visualizar",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            // Botón morado con icono de play
            Button(
                onClick = onHowItWorksClick,
                colors = ButtonDefaults.buttonColors(containerColor = Purple60),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(0.8f)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("file:///android_asset/inicio/play.svg")
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentDescription = "Play",
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Cómo funciona",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
        // Barra de navegación inferior
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            NavBard(
                toCamera = navigateToCamera,
                toTutorial = null,
                toCatalog = navigateToCatalog,
                toProfile = navigateToProfile,
                toConfiguration = navigateToConfiguration,
            )
        }
    }
} 