package com.app.homear.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.homear.R
import com.app.homear.core.navigation.NavigationWrapper
import com.app.homear.ui.theme.HomeARTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HomeARTheme {
                var showSplash by remember { mutableStateOf(true) }

                Crossfade(targetState = showSplash) { isSplash ->
                    if (isSplash) {
                        SplashScreenWithZoom {
                            showSplash = false
                        }
                    } else {
                        Box(modifier = Modifier.systemBarsPadding()) {
                            NavigationWrapper()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreenWithZoom(onSplashFinished: () -> Unit) {
    val scale = remember { Animatable(1f) }

    val pulseAnim by rememberInfiniteTransition().animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        delay(2000L)
        scale.animateTo(
            targetValue = 2f,
            animationSpec = tween(500, easing = EaseInOut)
        )
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7E1B75)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo decorAR",
            modifier = Modifier
                .size(150.dp)
                .graphicsLayer(
                    scaleX = pulseAnim * scale.value,
                    scaleY = pulseAnim * scale.value
                )
        )
    }
}
