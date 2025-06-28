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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.app.homear.ui.component.NavBard
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.google.ar.core.Session
import com.google.ar.core.Config
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberView
import io.github.sceneview.SceneView
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.rememberOnGestureListener
import kotlinx.coroutines.delay
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
            val lifecycleOwner = LocalLifecycleOwner.current
            val engine = rememberEngine()
            val modelLoader = rememberModelLoader(engine)
            val materialLoader = rememberMaterialLoader(engine)
            val childNodes = rememberNodes()
            val view = rememberView(engine)
            val collisionSystem = rememberCollisionSystem(view)
            val gestureListener = rememberOnGestureListener(
                onSingleTapConfirmed = { motionEvent, node ->
                    // Verificar si se tocó el botón de eliminar 3D
                    if (node != null && viewModel.isDeleteButtonNode(node)) {
                        viewModel.handleDeleteButtonTouch(childNodes)
                        Log.d("AR_DEBUG", "Botón de eliminar 3D tocado")
                    } else {
                        // Lógica principal basada en el modo activo
                        if (viewModel.isMeasuring.value) {
                            // MODO MEDICIÓN: Colocar puntos de medición
                            val anchor = when (node) {
                                null -> {
                                    val hitResults =
                                        viewModel.frame.value?.hitTest(motionEvent.x, motionEvent.y)
                                    hitResults?.firstOrNull {
                                        it.isValid(depthPoint = true, point = true)
                                    }?.createAnchorOrNull()
                                }
                                else -> null
                            }

                            if (anchor != null) {
                                when {
                                    viewModel.firstAnchor.value == null -> {
                                        // Primer punto de medición
                                        viewModel.firstAnchor.value = anchor
                                        val pointNode = viewModel.createMeasurementPointNode(
                                            engine = engine,
                                            materialLoader = materialLoader,
                                            anchor = anchor
                                        )
                                        childNodes += pointNode
                                        viewModel.measurementPoints.add(pointNode)
                                        Log.d("AR_DEBUG", "Primer punto de medición colocado")
                                    }

                                    viewModel.secondAnchor.value == null -> {
                                        // Segundo punto de medición
                                        viewModel.secondAnchor.value = anchor
                                        val pointNode = viewModel.createMeasurementPointNode(
                                            engine = engine,
                                            materialLoader = materialLoader,
                                            anchor = anchor
                                        )
                                        childNodes += pointNode
                                        viewModel.measurementPoints.add(pointNode)

                                        // Calcular distancia
                                        val distance = sqrt(
                                            (viewModel.firstAnchor.value!!.pose.tx() - anchor.pose.tx()).let { it * it } +
                                                    (viewModel.firstAnchor.value!!.pose.ty() - anchor.pose.ty()).let { it * it } +
                                                    (viewModel.firstAnchor.value!!.pose.tz() - anchor.pose.tz()).let { it * it }
                                        )
                                        viewModel.measuredDistance.value = distance
                                        Log.d(
                                            "AR_DEBUG",
                                            "Segundo punto colocado - Distancia: ${distance}m"
                                        )
                                    }

                                    viewModel.isCalculatingArea.value && viewModel.thirdAnchor.value == null -> {
                                        // Tercer punto para área
                                        viewModel.thirdAnchor.value = anchor
                                        val pointNode = viewModel.createMeasurementPointNode(
                                            engine = engine,
                                            materialLoader = materialLoader,
                                            anchor = anchor
                                        )
                                        childNodes += pointNode
                                        viewModel.measurementPoints.add(pointNode)

                                        // Calcular área
                                        val area = viewModel.calculateRectangleArea(
                                            viewModel.firstAnchor.value!!,
                                            viewModel.secondAnchor.value!!,
                                            anchor
                                        )
                                        viewModel.measuredArea.value = area
                                        viewModel.measurementHistory.add(area)
                                        Log.d("AR_DEBUG", "Tercer punto colocado - Área: ${area}m²")
                                    }

                                    else -> {
                                        // Reiniciar medición
                                        viewModel.firstAnchor.value = anchor
                                        viewModel.secondAnchor.value = null
                                        viewModel.thirdAnchor.value = null
                                        viewModel.measuredDistance.value = null
                                        viewModel.measuredArea.value = null
                                        viewModel.areaSideDistance1.value = null
                                        viewModel.areaSideDistance2.value = null

                                        // Limpiar puntos anteriores
                                        viewModel.measurementPoints.forEach { childNodes.remove(it) }
                                        viewModel.measurementPoints.clear()

                                        // Colocar nuevo primer punto
                                        val pointNode = viewModel.createMeasurementPointNode(
                                            engine = engine,
                                            materialLoader = materialLoader,
                                            anchor = anchor
                                        )
                                        childNodes += pointNode
                                        viewModel.measurementPoints.add(pointNode)
                                        Log.d(
                                            "AR_DEBUG",
                                            "Medición reiniciada - Nuevo primer punto"
                                        )
                                    }
                                }
                            }
                        } else {
                            // MODO NORMAL: Verificar si se tocó un modelo existente (solo si NO estamos en modo baldosa)
                            val touchedModel = if (!viewModel.isCoatingMode.value) {
                                node?.let { viewModel.findPlacedModelByNode(it) }
                            } else null

                            if (touchedModel != null) {
                                // Se tocó un modelo existente - seleccionarlo
                                viewModel.selectPlacedModel(touchedModel, engine, materialLoader)
                                Log.d("AR_DEBUG", "Modelo seleccionado: ${touchedModel.id}")
                            } else {
                                // Lógica para crear modelos o baldosas
                                val anchor = when (node) {
                                    null -> {
                                        val hitResults = viewModel.frame.value?.hitTest(
                                            motionEvent.x,
                                            motionEvent.y
                                        )
                                        hitResults?.firstOrNull {
                                            it.isValid(
                                                depthPoint = true,
                                                point = true
                                            )
                                        }?.createAnchorOrNull()
                                    }

                                    else -> null
                                }

                                if (anchor != null) {
                                    if (viewModel.isCoatingMode.value) {
                                        // Modo baldosa: crear baldosa automáticamente
                                        val frame = viewModel.frame.value
                                        val session = viewModel.session.value
                                        if (frame != null && session != null) {
                                            // Si ya existe una baldosa, no permitir más
                                            if (viewModel.tileNode == null) {
                                                val newTileNode = viewModel.createTiledCoatingNode(
                                                    engine = engine,
                                                    modelLoader = modelLoader,
                                                    materialLoader = materialLoader,
                                                    anchor = anchor,
                                                    planeExtentX = 1.0f,
                                                    planeExtentZ = 1.0f,
                                                    session = session
                                                )
                                                newTileNode?.let { childNodes += it }
                                            }
                                        }
                                    } else {
                                        // Modo normal: crear modelo seleccionado
                                        childNodes += viewModel.createAnchorNode(
                                            engine = engine,
                                            modelLoader = modelLoader,
                                            materialLoader = materialLoader,
                                            modelInstances = viewModel.modelInstances,
                                            anchor = anchor
                                        )
                                        // Deseleccionar cualquier modelo previamente seleccionado
                                        viewModel.selectPlacedModel(null, engine, materialLoader)
                                    }
                                }
                            }
                        }
                    }
                }
            )

            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_PAUSE) {
                        childNodes.forEach { node ->
                            try {
                                (node as? AnchorNode)?.anchor?.detach()
                            } catch (e: Exception) {
                                Log.w(
                                    "AR_DEBUG",
                                    "Error desconectando anchor en pausa: ${e.message}"
                                )
                            }
                        }
                        childNodes.clear()
                        Log.d("AR_DEBUG", "Recursos AR limpiados en ON_PAUSE")
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                    childNodes.forEach { node ->
                        try {
                            (node as? AnchorNode)?.anchor?.detach()
                        } catch (e: Exception) {
                            Log.w("AR_DEBUG", "Error desconectando anchor en dispose: ${e.message}")
                        }
                    }
                    childNodes.clear()
                    Log.d("AR_DEBUG", "Recursos AR limpiados en onDispose")
                }
            }

            ARScene(
                modifier = Modifier.fillMaxSize(),
                childNodes = childNodes,
                engine = engine,
                view = view,
                modelLoader = modelLoader,
                materialLoader = materialLoader,
                planeRenderer = viewModel.planeRenderer.value,
                onGestureListener = gestureListener,
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
                    viewModel.session.value = session
                    viewModel.frame.value = updatedFrame
                }
            )

            // NUEVO: Efecto para colocar baldosa automáticamente al entrar al modo baldosa
            LaunchedEffect(viewModel.isCoatingMode.value) {
                if (viewModel.isCoatingMode.value && viewModel.tileNode == null) {
                    Log.d(
                        "AR_DEBUG",
                        "Entrando a modo baldosa - buscando plano horizontal para colocar baldosa automáticamente"
                    )

                    // Intentar colocar baldosa automáticamente cada segundo hasta encontrar un plano
                    while (viewModel.isCoatingMode.value && viewModel.tileNode == null) {
                        val autoTileNode = viewModel.tryAutoPlaceTile(
                            engine = engine,
                            modelLoader = modelLoader,
                            materialLoader = materialLoader,
                            session = viewModel.session.value,
                            frame = viewModel.frame.value
                        )

                        if (autoTileNode != null) {
                            childNodes += autoTileNode
                            Log.d("AR_DEBUG", "Baldosa colocada automáticamente")
                            break
                        }

                        kotlinx.coroutines.delay(1000) // Esperar 1 segundo antes de intentar de nuevo
                    }
                }
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
                    } else if (viewModel.isCalculatingArea.value) {
                        when {
                            viewModel.firstAnchor.value == null -> "Modo Área: Toque el PRIMER punto"
                            viewModel.secondAnchor.value == null -> "Modo Área: Toque el SEGUNDO punto"
                            viewModel.thirdAnchor.value == null -> "Modo Área: Toque el TERCER punto"
                            else -> "Área calculada! Toque para reiniciar medición"
                        }
                    } else if (viewModel.isMeasuring.value) {
                        when {
                            viewModel.firstAnchor.value == null -> "Modo Medición: Toque el PRIMER punto"
                            viewModel.secondAnchor.value == null -> "Modo Medición: Toque el SEGUNDO punto"
                            else -> "Distancia medida! Toque para reiniciar medición"
                        }
                    } else if (childNodes.isEmpty()) {
                        "Busque un plano horizontal o vertical"
                    } else {
                        "Click para agregar"
                    }
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
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
                    viewModel.areaSideDistance1.value?.let { side1 ->
                        Text(
                            text = "Lado 1: ${"%.2f".format(side1)} m",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            color = Color.Cyan
                        )
                    }
                    viewModel.areaSideDistance2.value?.let { side2 ->
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

            if (viewModel.isCalculatingArea.value && viewModel.areaSideDistance1.value != null && viewModel.measuredArea.value == null) {
                viewModel.areaSideDistance1.value?.let { side1 ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(top = 640.dp),
                        text = "Lado 1: ${"%.2f".format(side1)} m",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        color = Color.Cyan
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
                            viewModel.isCalculatingArea.value = false
                            viewModel.removeAllTileNodes(childNodes)
                        } else {
                            viewModel.isCalculatingArea.value = false
                        }
                        viewModel.firstAnchor.value = null
                        viewModel.secondAnchor.value = null
                        viewModel.measuredDistance.value = null
                        viewModel.measuredArea.value = null
                        viewModel.sideDistance1.value = null
                        viewModel.sideDistance2.value = null
                        viewModel.areaSideDistance1.value = null
                        viewModel.areaSideDistance2.value = null
                        viewModel.measurementPoints.forEach { point ->
                            childNodes.remove(point)
                        }
                        viewModel.measurementPoints.clear()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.isMeasuring.value) Color(0xFF64B5F6) else Color.Gray
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(text = if (viewModel.isMeasuring.value) "Modo Medición: ON" else "Modo Medición: OFF")
                }

                if (viewModel.isMeasuring.value) {
                    Button(
                        onClick = {
                            viewModel.isCalculatingArea.value = !viewModel.isCalculatingArea.value
                            if (!viewModel.isCalculatingArea.value) {
                                viewModel.thirdAnchor.value = null
                                viewModel.measuredArea.value = null
                                viewModel.areaSideDistance1.value = null
                                viewModel.areaSideDistance2.value = null
                                viewModel.measurementPoints.forEach { point ->
                                    childNodes.remove(point)
                                }
                                viewModel.measurementPoints.clear()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.isCalculatingArea.value) Color(0xFF2196F3) else Color(
                                0xFF4CAF50
                            )
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(text = if (viewModel.isCalculatingArea.value) "Modo Área: ON" else "Modo Área: OFF")
                    }
                }

                Button(
                    onClick = {
                        viewModel.isCoatingMode.value = !viewModel.isCoatingMode.value
                        if (!viewModel.isCoatingMode.value) {
                            // Solo cuando salimos del modo baldosa: eliminar la baldosa si existe
                            if (viewModel.tileNode != null) {
                                childNodes.remove(viewModel.tileNode)
                                (viewModel.tileNode as? AnchorNode)?.anchor?.detach()
                                viewModel.resetTileNode()
                                Log.d(
                                    "AR_DEBUG",
                                    "Modo baldosa desactivado - baldosa eliminada"
                                )
                            }
                        } else {
                            viewModel.isMeasuring.value = false
                            viewModel.isCalculatingArea.value = false
                            viewModel.firstAnchor.value = null
                            viewModel.secondAnchor.value = null
                            viewModel.measuredDistance.value = null
                            viewModel.measuredArea.value = null
                            viewModel.sideDistance1.value = null
                            viewModel.sideDistance2.value = null
                            viewModel.areaSideDistance1.value = null
                            viewModel.areaSideDistance2.value = null
                            viewModel.measurementPoints.forEach { point ->
                                childNodes.remove(point)
                            }
                            viewModel.measurementPoints.clear()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.isCoatingMode.value) Color(0xFFFF9800) else Color.Gray
                    ),
                    modifier = Modifier.padding(top = 8.dp)
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
