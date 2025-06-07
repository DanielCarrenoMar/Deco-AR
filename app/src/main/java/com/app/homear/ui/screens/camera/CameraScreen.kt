package com.app.homear.ui.screens.camera

import android.util.Log
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.app.homear.ui.component.NavBard
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import kotlin.math.sqrt
import io.github.sceneview.node.SphereNode

private const val kModelFile = "models/apple.glb"
private const val kMaxModelInstances = 10

fun isArCoreSupported(context: Context): Boolean {
    val availability = ArCoreApk.getInstance().checkAvailability(context)
    return availability.isSupported
}

@Composable
fun HomeScreen(
    navigateToCatalog: () -> Unit,
) {
    val context = LocalContext.current
    val haveAr by remember { mutableStateOf(isArCoreSupported(context)) }

    if (haveAr) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            val engine = rememberEngine()
            val modelLoader = rememberModelLoader(engine)
            val materialLoader = rememberMaterialLoader(engine)
            val cameraNode = rememberARCameraNode(engine)
            val childNodes = rememberNodes()
            val view = rememberView(engine)
            val collisionSystem = rememberCollisionSystem(view)

            val planeRenderer = remember { mutableStateOf(true) }

            val modelInstances = remember { mutableListOf<ModelInstance>() }
            var trackingFailureReason by remember { mutableStateOf<TrackingFailureReason?>(null) }
            var frame by remember { mutableStateOf<Frame?>(null) }

            var isMeasuring by remember { mutableStateOf(false) }
            var firstAnchor: Anchor? by remember { mutableStateOf(null) }
            var secondAnchor: Anchor? by remember { mutableStateOf(null) }
            var measuredDistance by remember { mutableStateOf<Float?>(null) }
            val measurementHistory = remember { mutableStateListOf<Float>() }
            var showHistory by remember { mutableStateOf(false) }
            val measurementPoints = remember { mutableListOf<AnchorNode>() }

            ARScene(
                modifier = Modifier.fillMaxSize(),
                childNodes = childNodes,
                engine = engine,
                view = view,
                modelLoader = modelLoader,
                collisionSystem = collisionSystem,
                sessionConfiguration = { session, config ->
                    config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                    config.depthMode =
                        if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) Config.DepthMode.AUTOMATIC
                        else Config.DepthMode.DISABLED
                    config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                    config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                },
                cameraNode = cameraNode,
                planeRenderer = planeRenderer.value,
                onTrackingFailureChanged = {
                    trackingFailureReason = it
                },
                onSessionUpdated = { session, updatedFrame ->
                    frame = updatedFrame
                },
                onGestureListener = rememberOnGestureListener(
                    onSingleTapConfirmed = { motionEvent, node ->
                        if (node == null) {
                            val hitResults = frame?.hitTest(motionEvent.x, motionEvent.y)
                            hitResults?.firstOrNull {
                                it.isValid(depthPoint = true, point = true)
                            }?.createAnchorOrNull()?.let { anchor ->
                                if (isMeasuring) {
                                    if (firstAnchor == null) {
                                        firstAnchor = anchor
                                        // Crear y agregar punto visual para el primer anclaje
                                        val pointNode = createMeasurementPointNode(
                                            engine = engine,
                                            materialLoader = materialLoader,
                                            anchor = anchor
                                        )
                                        childNodes += pointNode
                                        measurementPoints.add(pointNode)
                                    } else {
                                        secondAnchor = anchor
                                        // Crear y agregar punto visual para el segundo anclaje
                                        val pointNode = createMeasurementPointNode(
                                            engine = engine,
                                            materialLoader = materialLoader,
                                            anchor = anchor
                                        )
                                        childNodes += pointNode
                                        measurementPoints.add(pointNode)

                                        val pose1 = firstAnchor!!.pose
                                        val pose2 = secondAnchor!!.pose
                                        val dx = pose1.tx() - pose2.tx()
                                        val dy = pose1.ty() - pose2.ty()
                                        val dz = pose1.tz() - pose2.tz()
                                        measuredDistance = sqrt(dx * dx + dy * dy + dz * dz)
                                        measuredDistance?.let { measurementHistory.add(it) }
                                        firstAnchor = null
                                        secondAnchor = null
                                    }
                                } else {
                                    childNodes += createAnchorNode(
                                        engine = engine,
                                        modelLoader = modelLoader,
                                        materialLoader = materialLoader,
                                        modelInstances = modelInstances,
                                        anchor = anchor
                                    )
                                }
                            }
                        }
                    })
            )

            // Puntero visual en forma de cruz en el centro de la pantalla
            val pointerColor = if (isMeasuring) Color.Cyan else Color.Red
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
                text = trackingFailureReason?.getDescription(LocalContext.current) ?: if (childNodes.isEmpty()) {
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



            measuredDistance?.let { distance ->
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(60.dp)
                    .padding(bottom = 70.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Button(
                    onClick = { planeRenderer.value = !planeRenderer.value },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (planeRenderer.value) Color.Green else Color.Red
                    )
                ) {
                    Text(text = if (planeRenderer.value) "Desactivar Plano" else "Activar Plano")
                }

                Button(
                    onClick = {
                        isMeasuring = !isMeasuring
                        firstAnchor = null
                        secondAnchor = null
                        measuredDistance = null
                        // Limpiar puntos de medición cuando se desactiva el modo
                        if (!isMeasuring) {
                            measurementPoints.forEach { point ->
                                childNodes.remove(point)
                            }
                            measurementPoints.clear()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMeasuring) Color(0xFF64B5F6) else Color.Gray
                    )
                ) {
                    Text(text = if (isMeasuring) "Modo MediciÃ³n: ON" else "Modo MediciÃ³n: OFF")
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 96.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Button(onClick = { showHistory = !showHistory }) {
                        Text(text = "Historial")
                    }
                    if (showHistory) {
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
                                    items(measurementHistory) { dist ->
                                        Text(
                                            text = "${"%.2f".format(dist)} m",
                                            color = Color.White,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                                Button(
                                    onClick = { measurementHistory.clear() },
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
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "ARCore no estÃ¡ soportado en este dispositivo.")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().zIndex(1f),
        verticalArrangement = Arrangement.Bottom
    ) {
        NavBard(
            items = listOf(
                NavBard.NavBarItem(
                    title = "Home",
                    icon = -1,
                    onClick = null
                ),
                NavBard.NavBarItem(
                    title = "Catalogo",
                    icon = -1,
                    onClick = navigateToCatalog
                )
            )
        )
    }
}

fun createAnchorNode(
    engine: Engine,
    modelLoader: ModelLoader,
    materialLoader: MaterialLoader,
    modelInstances: MutableList<ModelInstance>,
    anchor: Anchor
): AnchorNode {
    Log.d("AR_DEBUG", "Entrando a createAnchorNode")

    val anchorNode = AnchorNode(engine = engine, anchor = anchor)
    val modelNode = ModelNode(
        modelInstance = modelInstances.apply {
            if (isEmpty()) {
                Log.d("AR_DEBUG", "Cargando nuevo modelo desde archivo: $kModelFile")
                this += modelLoader.createInstancedModel(kModelFile, kMaxModelInstances)
            }
        }.removeAt(modelInstances.size - 1),
        scaleToUnits = 0.5f
    ).apply {
        isEditable = true
    }
    val boundingBoxNode = CubeNode(
        engine,
        size = modelNode.extents,
        center = modelNode.center,
        materialInstance = materialLoader.createColorInstance(Color.White.copy(alpha = 0.5f))
    ).apply {
        isVisible = false
    }
    modelNode.addChildNode(boundingBoxNode)
    anchorNode.addChildNode(modelNode)

    listOf(modelNode, anchorNode).forEach {
        it.onEditingChanged = { editingTransforms ->
            boundingBoxNode.isVisible = editingTransforms.isNotEmpty()
        }
    }
    Log.d("AR_DEBUG", "createAnchorNode completado exitosamente")
    return anchorNode
}

fun createMeasurementPointNode(
    engine: Engine,
    materialLoader: MaterialLoader,
    anchor: Anchor
): AnchorNode {
    val anchorNode = AnchorNode(engine = engine, anchor = anchor)
    val sphereNode = SphereNode(
        engine = engine,
        radius = 0.05f, // Tamaño pequeño para el punto
        materialInstance = materialLoader.createColorInstance(Color.Cyan.copy(alpha = 0.8f))
    )
    anchorNode.addChildNode(sphereNode)
    return anchorNode
}