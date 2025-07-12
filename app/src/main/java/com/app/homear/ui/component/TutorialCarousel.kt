package com.app.homear.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.toColorInt

@Composable
fun TutorialCarousel(
    isDialogOpen: Boolean,
    onDismiss: () -> Unit,
    steps: List<String>
) {
    var currentStep by remember { mutableStateOf(0) }

    if (isDialogOpen && steps.isNotEmpty()) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .clip(RoundedCornerShape(16.dp)),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Título, botón cerrar y progreso
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Botón cerrar
                        IconButton(
                            onClick = { onDismiss() },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color("#8F006D".toColorInt())
                            )
                        }
                        
                        // Título y progreso
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Tutorial",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color("#8F006D".toColorInt())
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Paso ${currentStep + 1} de ${steps.size}",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            LinearProgressIndicator(
                                progress = (currentStep + 1).toFloat() / steps.size,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = Color("#8F006D".toColorInt()),
                                trackColor = Color("#8F006D".toColorInt()).copy(alpha = 0.2f)
                            )
                        }
                    }

                    // Contenido del paso actual con scroll
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = currentStep,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) togetherWith
                                        fadeOut(animationSpec = tween(300))
                            },
                            label = "Step Animation"
                        ) { step ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = steps[step],
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 24.sp
                                )
                            }
                        }
                    }

                    // Navegación y botones
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Botón anterior
                            IconButton(
                                onClick = { 
                                    if (currentStep > 0) currentStep-- 
                                },
                                enabled = currentStep > 0
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowLeft,
                                    contentDescription = "Anterior",
                                    tint = if (currentStep > 0) 
                                        Color("#8F006D".toColorInt()) 
                                    else 
                                        Color.Gray
                                )
                            }

                            // Indicadores de paso
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                repeat(steps.size) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (index == currentStep)
                                                    Color("#8F006D".toColorInt())
                                                else
                                                    Color("#8F006D".toColorInt()).copy(alpha = 0.2f)
                                            )
                                    )
                                }
                            }

                            // Botón siguiente
                            IconButton(
                                onClick = { 
                                    if (currentStep < steps.size - 1) currentStep++ 
                                },
                                enabled = currentStep < steps.size - 1
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Siguiente",
                                    tint = if (currentStep < steps.size - 1) 
                                        Color("#8F006D".toColorInt()) 
                                    else 
                                        Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón de finalizar/siguiente
                        Button(
                            onClick = {
                                if (currentStep < steps.size - 1) {
                                    currentStep++
                                } else {
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color("#8F006D".toColorInt())
                            ),
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(48.dp)
                                .border(
                                    width = 2.dp,
                                    color = Color("#8F006D".toColorInt()),
                                    shape = RoundedCornerShape(percent = 50)
                                )
                        ) {
                            Text(
                                text = if (currentStep < steps.size - 1) "Siguiente" else "Finalizar",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
} 