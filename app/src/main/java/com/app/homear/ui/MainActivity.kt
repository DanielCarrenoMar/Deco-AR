package com.app.homear.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.app.homear.core.navigation.NavigationWrapper
import com.app.homear.ui.theme.HomeARTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeARTheme {
                SplashScreenWithZoom{
                    NavigationWrapper()
                }
            }
        }
    }
}

@Composable
fun SplashScreenWithZoom(onSplashFinished: @Composable () -> Unit) {
    val scale = remember { Animatable(1f) }
    val pulseTransition = rememberInfiniteTransition()
    val pulseAnim by pulseTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2000)
        scale.animateTo(
            targetValue = 2f,
            animationSpec = tween(durationMillis = 500, easing = EaseInOut)
        )
        showContent = true
    }

    if (showContent) {
        onSplashFinished()
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF7E1B75)),
            contentAlignment = Alignment.Center
        ) {
            Text(text="LOGO")
        }
    }
}