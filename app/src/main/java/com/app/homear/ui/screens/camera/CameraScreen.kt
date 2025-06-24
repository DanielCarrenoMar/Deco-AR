package com.app.homear.ui.screens.camera

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
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
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import kotlin.math.sqrt

fun View.findARSceneView(): ARSceneView? {
    if (this is ARSceneView) {
        return this
    }
    if (this is ViewGroup) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val arSceneView = child.findARSceneView()
            if (arSceneView != null) {
                return arSceneView
            }
        }
    }
    return null
}

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
    val engine = rememberEngine()
    val view = rememberView(engine)
    val currentView = LocalView.current

    viewModel.capturedBitmap.value?.let { bitmap ->
        AlertDialog(
            onDismissRequest = { viewModel.capturedBitmap.value = null },
            title = { Text("Vista Previa") },
            text = {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Captura de pantalla",
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.capturedBitmap.value?.let { bmp ->
                        viewModel.saveBitmapToGallery(context, bmp)
                    }
                    viewModel.capturedBitmap.value = null
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.capturedBitmap.value = null }) {
                    Text("Descartar")
                }
            }
        )
    }

    if (haveAr) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            val modelLoader = rememberModelLoader(engine)
            val materialLoader = rememberMaterialLoader(engine)
            val cameraNode = rememberARCameraNode(engine)
            val childNodes = rememberNodes()
            val collisionSystem = rememberCollisionSystem(view)

            ARScene(
                modifier = Modifier.fillMaxSize(),
                childNodes = childNodes,
                engine = engine,
                view = view,
                modelLoader = modelLoader,
                collisionSystem = collisionSystem,
                sessionConfiguration = { session, config ->
                    config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL

                    config.depthMode = when {
                        session.isDepthModeSupported(Config.DepthMode.AUTOMATIC) -> Config.DepthMode.AUTOMATIC
                        session.isDepthModeSupported(Config.DepthMode.RAW_DEPTH_ONLY) -> Config.DepthMode.RAW_DEPTH_ONLY
                        else -> Config.DepthMode.DISABLED
                    }

                    config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                    config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                    config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                },
                cameraNode = cameraNode,
                planeRenderer = viewModel.planeRenderer.value,
                onTrackingFailureChanged = {
                    viewModel.trackingFailureReason.value = it
                },
                onSessionUpdated = { session, updatedFrame ->
                    viewModel.frame.value = updatedFrame
                    val planesThisFrame = updatedFrame.getUpdatedTrackables(Plane::class.java)

                    val validPlanes = planesThisFrame.filter { plane ->
                        plane.trackingState == TrackingState.TRACKING &&
                                plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING &&
                                plane.extentX > 0.5f && plane.extentZ > 0.5f // Mínimo 50cm x 50cm
                    }

                    validPlanes.forEach { plane ->
                        val planeId = plane.hashCode()
                        if (!viewModel.processedPlanes.contains(planeId)) {
                            viewModel.processedPlanes.add(planeId)

                            Log.d("AR_DEBUG", "Procesando plano nuevo: ${plane.extentX}m x ${plane.extentZ}m")

                            try {
                                // Verificar que el plano sea estable antes de crear baldosas
                                if (plane.extentX > 0.8f && plane.extentZ > 0.8f) {
                                    val centerPose = plane.centerPose
                                    val anchor = session.createAnchor(centerPose)

                                    // Crear revestimiento con baldosas distribuidas uniformemente
                                    val coatingNodes = viewModel.createTiledCoatingNode(
                                        engine = engine,
                                        modelLoader = modelLoader,
                                        materialLoader = materialLoader,
                                        anchor = anchor,
                                        planeExtentX = plane.extentX,
                                        planeExtentZ = plane.extentZ,
                                        session = session
                                    )

                                    // Añadir todas las baldosas a la escena solo si se crearon exitosamente
                                    if (coatingNodes.isNotEmpty()) {
                                        childNodes.addAll(coatingNodes)
                                        Log.d("AR_DEBUG", "Revestimiento aplicado exitosamente a plano de ${plane.extentX}m x ${plane.extentZ}m")
                                    }
                                } else {
                                    Log.d("AR_DEBUG", "Plano muy pequeño para revestimiento: ${plane.extentX}m x ${plane.extentZ}m")
                                }
                            } catch (e: Exception) {
                                Log.e("AR_DEBUG", "Error aplicando revestimiento a plano: ${e.message}")
                                // Remover plano de procesados para permitir reintento
                                viewModel.processedPlanes.remove(planeId)
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
                    }
                )
            )

            // Puntero visual en forma de cruz en el centro de la pantalla
            val pointerColor = if (viewModel.isMeasuring.value) Color.Cyan else Color.Red
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp)
                    .zIndex(2f),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .background(pointerColor)
                )
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .width(24.dp)
                        .background(pointerColor)
                )
            }

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
                    "Modo Baldosa: Detectando suelos para colocar baldosa individual..."
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
            /*
            // NUEVO: Indicador de estado de la cámara
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 80.dp, start = 16.dp)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(8.dp)
            ) {
                Column {
                    // Estado de tracking
                    val trackingStatus = viewModel.checkAndImproveTracking(viewModel.frame.value)
                    Text(
                        text = "Tracking: ${if (trackingStatus) "✅ Activo" else "❌ Inactivo"}",
                        color = if (trackingStatus) Color.Green else Color.Red,
                        fontSize = 12.sp
                    )
                    
                    // Estado de iluminación
                    val lightingStatus = viewModel.checkLightingConditions(viewModel.frame.value)
                    Text(
                        text = "Luz: $lightingStatus",
                        color = when {
                            lightingStatus.contains("buena") -> Color.Green
                            lightingStatus.contains("moderada") -> Color.Yellow
                            else -> Color.Red
                        },
                        fontSize = 12.sp
                    )
                    
                    // Estado de estabilidad
                    val stabilityStatus = viewModel.checkDeviceStability(viewModel.frame.value)
                    Text(
                        text = "Estabilidad: ${if (stabilityStatus) "✅ Estable" else "⚠️ Inestable"}",
                        color = if (stabilityStatus) Color.Green else Color.Yellow,
                        fontSize = 12.sp
                    )
                }
            }
            */

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
                        if (viewModel.isMeasuring.value) {
                            // Al activar modo medición, limpiar cualquier baldosa existente
                            viewModel.removeAllTileNodes(childNodes)
                        }
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
                    Text(text = if (viewModel.isMeasuring.value) "Modo Medicion: ON" else "Modo Medicion: OFF")
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
                            // Al desactivar modo revestimiento, eliminar todas las baldosas y limpiar recursos
                            viewModel.removeAllTileNodes(childNodes)
                            Log.d("AR_DEBUG", "Modo revestimiento desactivado - todas las baldosas eliminadas")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.isCoatingMode.value) Color(0xFFFF9800) else Color.Gray
                    )
                ) {
                    Text(text = if (viewModel.isCoatingMode.value) "Modo Baldosa: ON" else "Modo Baldosa: OFF")
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
                                            text = "${"%.2f".format(dist)} m",
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
                Column(horizontalAlignment = Alignment.End) {
                    ModelSelector(viewModel = viewModel)

                    Button(
                        onClick = {
                            viewModel.takeScreenshot(currentView.findARSceneView(), context)
                        },
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(text = "Foto")
                    }
                }
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


