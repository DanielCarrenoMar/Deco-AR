package com.app.homear.ui.screens.camera

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.homear.ui.component.NavBard
import com.google.ar.core.Config
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberView
import io.github.sceneview.rememberOnGestureListener
import kotlin.math.sqrt

@Composable
private fun ModelSelector(
    viewModel: CameraViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Button(
            onClick = { viewModel.isDropdownExpanded.value = !viewModel.isDropdownExpanded.value },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = viewModel.selectedModel.value?.name ?: "Seleccionar modelo",
                    modifier = Modifier.weight(3f)
                )
                Icon(
                    imageVector = if (viewModel.isDropdownExpanded.value) 
                        Icons.Default.KeyboardArrowUp 
                    else 
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expandir menú"
                )
            }
        }

        DropdownMenu(
            expanded = viewModel.isDropdownExpanded.value,
            onDismissRequest = { viewModel.isDropdownExpanded.value = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .width(250.dp)
        ) {
            viewModel.availableModels.forEach { model ->
                DropdownMenuItem(
                    text = { Text(text = model.name) },
                    onClick = {
                        Log.d("AR_DEBUG", "Seleccionando modelo: ${model.name}, path: ${model.modelPath}")
                        viewModel.selectedModel.value = model
                        viewModel.modelInstances.clear() // Limpiar instancias anteriores
                        viewModel.isDropdownExpanded.value = false
                    }
                )
            }
        }
    }
}

@Composable
fun CameraScreen(
    navigateToTutorial: () -> Unit,
    navigateToCatalog: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToConfiguration: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val haveAr by remember { mutableStateOf(viewModel.isArCoreSupported(context)) }

    if (haveAr) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            val engine = rememberEngine()
            val modelLoader = rememberModelLoader(engine)
            val materialLoader = rememberMaterialLoader(engine)
            val childNodes = rememberNodes()
            val view = rememberView(engine)
            val collisionSystem = rememberCollisionSystem(view)

            ARScene(
                modifier = Modifier.fillMaxSize(),
                childNodes = childNodes,
                engine = engine,
                view = view,
                modelLoader = modelLoader,
                planeRenderer = viewModel.planeRenderer.value && !viewModel.isCoatingMode.value,
                collisionSystem = collisionSystem,
                sessionConfiguration = { session, config ->
                    config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                    config.depthMode =
                        if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) Config.DepthMode.AUTOMATIC
                        else Config.DepthMode.DISABLED
                    config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                    config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                },
                onTrackingFailureChanged = {
                    viewModel.trackingFailureReason.value = it
                },
                onSessionUpdated = { session, updatedFrame ->
                    viewModel.frame.value = updatedFrame
                    
                    // Colocación automática de revestimiento escalado en modo revestimiento
                    if (viewModel.isCoatingMode.value) {
                        updatedFrame.getUpdatedTrackables(Plane::class.java).forEach { plane ->
                            if (plane.trackingState == TrackingState.TRACKING && 
                                plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING) {
                                
                                // Verificar si ya hemos colocado revestimiento en este plano
                                val planeId = plane.hashCode()
                                if (!viewModel.processedPlanes.contains(planeId)) {
                                    viewModel.processedPlanes.add(planeId)
                                    
                                    // Obtener las dimensiones del plano detectado
                                    val planeExtentX = plane.extentX
                                    val planeExtentZ = plane.extentZ
                                    
                                    // Crear anchor en el centro del plano
                                    val centerPose = plane.centerPose
                                    val anchor = session.createAnchor(centerPose)
                                    
                                    // Crear revestimiento con baldosas distribuidas uniformemente
                                    val coatingNodes = viewModel.createTiledCoatingNode(
                                        engine = engine,
                                        modelLoader = modelLoader,
                                        materialLoader = materialLoader,
                                        anchor = anchor,
                                        planeExtentX = planeExtentX,
                                        planeExtentZ = planeExtentZ,
                                        session = session
                                    )
                                    
                                    // Añadir todas las baldosas a la escena
                                    childNodes.addAll(coatingNodes)
                                    
                                    Log.d("AR_DEBUG", "Revestimiento aplicado a plano de ${planeExtentX}m x ${planeExtentZ}m")
                                }
                            }
                        }
                    }
                },
                onGestureListener = rememberOnGestureListener(
                    onSingleTapConfirmed = { motionEvent, node ->
                        if (node == null) {
                            val hitResults = viewModel.frame.value?.hitTest(motionEvent.x, motionEvent.y)
                            hitResults?.firstOrNull {
                                it.isValid(depthPoint = true, point = true)
                            }?.createAnchorOrNull()?.let { anchor ->
                                if (viewModel.isMeasuring.value) {
                                    if (viewModel.firstAnchor.value == null) {
                                        // Limpiar mediciones anteriores al iniciar nueva medición
                                        viewModel.measuredDistance.value = null
                                        viewModel.measuredArea.value = null
                                        viewModel.sideDistance1.value = null
                                        viewModel.sideDistance2.value = null
                                        
                                        viewModel.firstAnchor.value = anchor
                                        // Crear y agregar punto visual para el primer anclaje
                                        val pointNode = viewModel.createMeasurementPointNode(
                                            engine = engine,
                                            materialLoader = materialLoader,
                                            anchor = anchor
                                        )
                                        childNodes += pointNode
                                        viewModel.measurementPoints.add(pointNode)
                                    } else if (viewModel.secondAnchor.value == null) {
                                        viewModel.secondAnchor.value = anchor
                                        // Crear y agregar punto visual para el segundo anclaje
                                        val pointNode = viewModel.createMeasurementPointNode(
                                            engine = engine,
                                            materialLoader = materialLoader,
                                            anchor = anchor
                                        )
                                        childNodes += pointNode
                                        viewModel.measurementPoints.add(pointNode)

                                        // Calcular distancia entre primer y segundo punto
                                        val pose1 = viewModel.firstAnchor.value!!.pose
                                        val pose2 = viewModel.secondAnchor.value!!.pose
                                        val dx = pose1.tx() - pose2.tx()
                                        val dy = pose1.ty() - pose2.ty()
                                        val dz = pose1.tz() - pose2.tz()
                                        viewModel.measuredDistance.value = sqrt(dx * dx + dy * dy + dz * dz)
                                    } else {
                                        // Tercer punto - calcular área
                                        viewModel.thirdAnchor.value = anchor
                                        // Crear y agregar punto visual para el tercer anclaje
                                        val pointNode = viewModel.createMeasurementPointNode(
                                            engine = engine,
                                            materialLoader = materialLoader,
                                            anchor = anchor
                                        )
                                        childNodes += pointNode
                                        viewModel.measurementPoints.add(pointNode)

                                        // Calcular área del rectángulo
                                        viewModel.measuredArea.value = viewModel.calculateRectangleArea(
                                            viewModel.firstAnchor.value!!,
                                            viewModel.secondAnchor.value!!,
                                            viewModel.thirdAnchor.value!!
                                        )
                                        
                                        // Agregar área al historial y resetear puntos
                                        viewModel.measuredArea.value?.let { viewModel.measurementHistory.add(it) }
                                        viewModel.firstAnchor.value = null
                                        viewModel.secondAnchor.value = null
                                        viewModel.thirdAnchor.value = null
                                        // Las distancias y área se mantienen para mostrar el resultado completo
                                    }
                                } else if (viewModel.isCoatingMode.value) {
                                    // El modo revestimiento se maneja automáticamente en onSessionUpdated
                                    // No hacer nada en el tap manual cuando está en modo revestimiento
                                } else {
                                    childNodes += viewModel.createAnchorNode(
                                        engine = engine,
                                        modelLoader = modelLoader,
                                        materialLoader = materialLoader,
                                        modelInstances = viewModel.modelInstances,
                                        anchor = anchor
                                    )
                                }
                            }
                        }
                    })
            )

            Text(
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 32.dp, end = 32.dp),
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                color = Color.White,
                text = viewModel.trackingFailureReason.value?.getDescription(LocalContext.current) ?: 
                    if (viewModel.isCoatingMode.value) {
                        "Modo Revestimiento: Detectando suelos..."
                    } else if (viewModel.isMeasuring.value) {
                        when {
                            viewModel.firstAnchor.value == null -> "Modo Medición: Toque el PRIMER punto"
                            viewModel.secondAnchor.value == null -> "Modo Medición: Toque el SEGUNDO punto"
                            viewModel.thirdAnchor.value == null -> "Distancia medida! Toque TERCER punto para calcular área rectangular"
                            else -> "Calculando área rectangular..."
                        }
                    } else if (childNodes.isEmpty()) {
                        "Busque un plano horizontal o vertical"
                    } else {
                        "Click para agregar"
                    }
            )

            //Botón de screenshot
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val context = LocalContext.current

                Button(
                    modifier = Modifier
                        .align(Alignment.TopStart),
                    onClick = {
                        Toast.makeText(
                            context,
                            "Has tomado una foto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Text(text = "Foto")
                }
            }

            viewModel.measuredDistance.value?.let { distance ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 640.dp),
                    text = "Distancia: ${"%.2f".format(distance)} m",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = Color.Yellow
                )
            }

            viewModel.measuredArea.value?.let { area ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 610.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    viewModel.sideDistance1.value?.let { side1 ->
                        Text(
                            text = "Lado 1: ${"%.2f".format(side1)} m",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            color = Color.Cyan
                        )
                    }
                    viewModel.sideDistance2.value?.let { side2 ->
                        Text(
                            text = "Lado 2: ${"%.2f".format(side2)} m",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            color = Color.Cyan
                        )
                    }
                    Text(
                        text = "Área: ${"%.2f".format(area)} m²",
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        color = Color.Yellow
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(65.dp)
                    .padding(bottom = 70.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Button(
                    onClick = { viewModel.planeRenderer.value = !viewModel.planeRenderer.value },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.planeRenderer.value) Color.Green else Color.Red
                    )
                ) {
                    Text(text = if (viewModel.planeRenderer.value) "Desactivar Plano" else "Activar Plano")
                }

                Button(
                    onClick = {
                        viewModel.isMeasuring.value = !viewModel.isMeasuring.value
                        viewModel.firstAnchor.value = null
                        viewModel.secondAnchor.value = null
                        viewModel.thirdAnchor.value = null
                        viewModel.measuredDistance.value = null
                        viewModel.measuredArea.value = null
                        viewModel.sideDistance1.value = null
                        viewModel.sideDistance2.value = null
                        // Limpiar puntos de medición cuando se desactiva el modo
                        if (!viewModel.isMeasuring.value) {
                            viewModel.measurementPoints.forEach { point ->
                                childNodes.remove(point)
                            }
                            viewModel.measurementPoints.clear()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.isMeasuring.value) Color(0xFF64B5F6) else Color.Gray
                    )
                ) {
                    Text(text = if (viewModel.isMeasuring.value) "Modo Medición: ON" else "Modo Medición: OFF")
                }

                Button(
                    onClick = {
                        viewModel.isCoatingMode.value = !viewModel.isCoatingMode.value
                        // Si se activa modo revestimiento, desactivar medición
                        if (viewModel.isCoatingMode.value) {
                            viewModel.isMeasuring.value = false
                            viewModel.firstAnchor.value = null
                            viewModel.secondAnchor.value = null
                            viewModel.thirdAnchor.value = null
                            viewModel.measuredDistance.value = null
                            viewModel.measuredArea.value = null
                            viewModel.sideDistance1.value = null
                            viewModel.sideDistance2.value = null
                            viewModel.measurementPoints.forEach { point ->
                                childNodes.remove(point)
                            }
                            viewModel.measurementPoints.clear()
                        } else {
                            // Al desactivar modo revestimiento, limpiar planos procesados
                            viewModel.processedPlanes.clear()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.isCoatingMode.value) Color(0xFFFF9800) else Color.Gray
                    )
                ) {
                    Text(text = if (viewModel.isCoatingMode.value) "Modo Revestimiento: ON" else "Modo Revestimiento: OFF")
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 148.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Button(onClick = { viewModel.showHistory.value = !viewModel.showHistory.value }) {
                        Text(text = "Historial")
                    }
                    if (viewModel.showHistory.value) {
                        Box(
                            modifier = Modifier
                                .background(Color.DarkGray.copy(alpha = 0.9f))
                                .padding(8.dp)
                                .zIndex(2f)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.Start
                            ) {
                                LazyColumn(
                                    modifier = Modifier
                                        .height(150.dp)
                                        .width(200.dp)
                                ) {
                                    items(viewModel.measurementHistory) { dist ->
                                        Text(
                                            text = "${"%.2f".format(dist)} m²",
                                            color = Color.White,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                                Button(
                                    onClick = { viewModel.measurementHistory.clear() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text(text = "Limpiar", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // Menú desplegable de modelos
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 16.dp)
                    .zIndex(2f)
            ) {
                ModelSelector(viewModel = viewModel)
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "ARCore no está soportado en este dispositivo.")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f),
        verticalArrangement = Arrangement.Bottom
    ) {
        NavBard(
            toCamera = null,
            toTutorial = navigateToTutorial,
            toCatalog = navigateToCatalog,
            toProfile = navigateToProfile,
            toConfiguration = navigateToConfiguration,
        )
    }
}
