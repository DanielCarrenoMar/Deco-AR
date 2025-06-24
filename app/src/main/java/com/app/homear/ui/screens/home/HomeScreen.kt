package com.app.homear.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.core.view.WindowInsetsCompat
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.app.homear.ui.component.NavBard
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex

// Colores personalizados
val CustomPurple = Color(0xFF54124E)
val CustomGreen = Color(0xFF00664B)

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
            .background(Color(0xFFEEEEEE))
            .padding(WindowInsets.statusBars.asPaddingValues())
            .drawBehind {
                // Tamaño proporcional del círculo decorativo
                val radius = size.width * 0.6f
                // Posicionamos el centro del círculo fuera del borde derecho y arriba
                val centerX = size.width * 0.9f
                val centerY = radius * 0.87f

                drawCircle(
                    color = CustomGreen,
                    radius = radius,
                    center = Offset(centerX, centerY)
                )
            }
    ) {
        // Título decorAR
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(4f)
                    .background(CustomPurple, RoundedCornerShape(topEnd = 15.dp, bottomEnd = 15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "decorAR",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp,
                )
            }
        }

        // Imágenes decorativas
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
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f)
                    .align(Alignment.Center)
                    .offset(x = (-76f).dp, y = (-86f).dp),
            )
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("file:///android_asset/inicio/furniture-background.svg")
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = "Mueble",
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f)
                    .align(Alignment.Center)
                    .offset(x = (50f).dp, y = (-97f).dp)
            )
        }

        // Textos y botón
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp, start = 20.dp)
                .fillMaxWidth()
                .padding(horizontal = 38.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Diseña tu espacio en segundos",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Color(0xFF3E3E3E),
                lineHeight = 38.sp,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Solo necesitas tu cámara para comenzar a visualizar.",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF535353),
                lineHeight = 28.sp,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onHowItWorksClick,
                colors = ButtonDefaults.buttonColors(containerColor = CustomPurple),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(0.6f)
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
                    fontSize = 13.sp
                )
            }
        }

        // Barra de navegación inferior
        Column(
            modifier = Modifier.fillMaxSize(),
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


