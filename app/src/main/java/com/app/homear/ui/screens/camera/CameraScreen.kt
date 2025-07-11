package com.app.homear.ui.screens.camera

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.app.homear.ui.component.NavBar
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.google.ar.core.Session
import com.google.ar.core.Config
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberView
import io.github.sceneview.SceneView
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import androidx.compose.foundation.layout.WindowInsets
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.sceneview.rememberOnGestureListener
import kotlinx.coroutines.delay
import kotlin.math.sqrt
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import android.view.SurfaceView
import android.view.PixelCopy
import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.app.homear.R
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import com.app.homear.core.utils.SharedPreferenceHelper
import org.json.JSONArray

// Función para encontrar la vista ARSceneView
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

// Función para encontrar la SurfaceView
fun View.findSurfaceView(): SurfaceView? {
    if (this is SurfaceView) return this
    if (this is ViewGroup) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val result = child.findSurfaceView()
            if (result != null) return result
        }
    }
    return null
}

// Función para capturar AR con PixelCopy
fun captureARWithPixelCopy(
    activity: Activity,
    arSceneView: ARSceneView,
    onResult: (Bitmap?) -> Unit
) {
    val surfaceView = arSceneView.findSurfaceView()
    if (surfaceView == null) {
        Log.e("CameraScreen", "No se encontró SurfaceView en ARSceneView")
        onResult(null)
        return
    }

    Log.d("CameraScreen", "SurfaceView encontrada: ${surfaceView.width}x${surfaceView.height}")
    
    if (surfaceView.width <= 0 || surfaceView.height <= 0) {
        Log.e("CameraScreen", "SurfaceView no tiene dimensiones válidas")
        onResult(null)
        return
    }

    val bitmap = Bitmap.createBitmap(surfaceView.width, surfaceView.height, Bitmap.Config.ARGB_8888)
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        PixelCopy.request(
            surfaceView,
            bitmap,
            { copyResult ->
                when (copyResult) {
                    PixelCopy.SUCCESS -> {
                        Log.d("CameraScreen", "PixelCopy exitoso: ${bitmap.width}x${bitmap.height}")
                        onResult(bitmap)
                    }
                    1 -> { // ERROR_SOURCE_NOT_IN_VISIBLE_REGION
                        Log.e("CameraScreen", "Error: fuente no en región visible")
                        onResult(null)
                    }
                    2 -> { // ERROR_SOURCE_INVALID
                        Log.e("CameraScreen", "Error: fuente inválida")
                        onResult(null)
                    }
                    3 -> { // ERROR_DESTINATION_INVALID
                        Log.e("CameraScreen", "Error: destino inválido")
                        onResult(null)
                    }
                    4 -> { // ERROR_TIMEOUT
                        Log.e("CameraScreen", "Error: timeout")
                        onResult(null)
                    }
                    else -> {
                        Log.e("CameraScreen", "Error desconocido en PixelCopy: $copyResult")
                        onResult(null)
                    }
                }
            },
            Handler(Looper.getMainLooper())
        )
    } else {
        Log.e("CameraScreen", "PixelCopy requiere Android 7.0+ (API 24)")
        onResult(null)
    }
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
    navigateToSpaces: () -> Unit,
    navigateToConfiguration: () -> Unit,
    navigateToCreateSpace: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val sharedPrefHelper = remember { SharedPreferenceHelper(context) }
    val haveAr by remember { mutableStateOf(viewModel.isArCoreSupported(context)) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val engine = rememberEngine()
    val view = rememberView(engine)
    val currentView = LocalView.current
    var showConfirmSavedModal by remember { mutableStateOf(false) }


    // Función para recortar el bitmap y excluir elementos de UI
    fun cropBitmapToARContent(originalBitmap: Bitmap, rootView: View): Bitmap {
        try {
            Log.d("CameraScreen", "Recortando bitmap original: ${originalBitmap.width}x${originalBitmap.height}")
            
            // Calcular las dimensiones de recorte basadas en el tamaño de la pantalla
            val screenWidth = originalBitmap.width
            val screenHeight = originalBitmap.height
            
            // Definir las áreas a excluir (elementos de UI)
            val statusBarHeight = (screenHeight * 0.05).toInt() // 5% de la altura para barra de estado
            val topUIHeight = (screenHeight * 0.15).toInt() // 15% para elementos superiores
            val bottomUIHeight = (screenHeight * 0.20).toInt() // 20% para elementos inferiores
            val sideUIWidth = (screenWidth * 0.08).toInt() // 8% para elementos laterales
            
            val startX = sideUIWidth
            val startY = statusBarHeight + topUIHeight
            val endX = screenWidth - sideUIWidth
            val endY = screenHeight - bottomUIHeight
            
            // Verificar que las dimensiones sean válidas
            if (startX >= endX || startY >= endY) {
                Log.w("CameraScreen", "Dimensiones de recorte inválidas, retornando bitmap original")
                return originalBitmap
            }
            
            // Verificar que el área de recorte no sea muy pequeña
            val cropWidth = endX - startX
            val cropHeight = endY - startY
            if (cropWidth < 100 || cropHeight < 100) {
                Log.w("CameraScreen", "Área de recorte muy pequeña, retornando bitmap original")
                return originalBitmap
            }
            
            // Crear el bitmap recortado
            val croppedBitmap = Bitmap.createBitmap(
                originalBitmap, 
                startX, 
                startY, 
                cropWidth, 
                cropHeight
            )
            
            Log.d("CameraScreen", "Bitmap recortado: ${croppedBitmap.width}x${croppedBitmap.height}")
            Log.d("CameraScreen", "Área de recorte: ($startX, $startY) a ($endX, $endY)")
            return croppedBitmap
            
        } catch (e: Exception) {
            Log.e("CameraScreen", "Error al recortar bitmap: ${e.message}", e)
            return originalBitmap
        }
    }

    fun takeScreenshot() {
        try {
            Log.d("CameraScreen", "Iniciando captura de pantalla con PixelCopy...")
            
            val activity = context as? Activity
            if (activity == null) {
                Toast.makeText(context, "No se pudo obtener la actividad", Toast.LENGTH_SHORT).show()
                Log.e("CameraScreen", "Context no es una Activity")
                return
            }
            
            // Buscar la ARSceneView
            val sceneView = currentView.findARSceneView()
            if (sceneView == null) {
                Toast.makeText(context, "No se pudo encontrar la vista AR", Toast.LENGTH_SHORT).show()
                Log.e("CameraScreen", "ARSceneView no encontrada")
                return
            }
            
            Log.d("CameraScreen", "ARSceneView encontrada, iniciando PixelCopy...")
            
            // Usar PixelCopy para capturar la SurfaceView de ARCore
            captureARWithPixelCopy(activity, sceneView) { bitmap ->
                if (bitmap != null) {
                    // Recortar la imagen para excluir elementos de UI
                    val croppedBitmap = cropBitmapToARContent(bitmap, currentView.rootView)
                    capturedBitmap = croppedBitmap
                    Log.d("CameraScreen", "Captura con PixelCopy exitosa: ${croppedBitmap.width}x${croppedBitmap.height}")
                } else {
                    Log.e("CameraScreen", "PixelCopy falló")
                    Toast.makeText(context, "Error al capturar la pantalla", Toast.LENGTH_SHORT).show()
                }
            }
            
        } catch (e: Exception) {
            Log.e("CameraScreen", "Error al tomar la captura: ${e.message}", e)
            Toast.makeText(context, "Error al tomar la captura: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    capturedBitmap?.let { bitmap ->
        val context = LocalContext.current
        val saveImageToGallery: (Bitmap) -> Unit = { bmp ->
            try {
                val filename = "AR_${System.currentTimeMillis()}.jpg"
                val fos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val resolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DecorAR")
                    }
                    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    if (imageUri != null) {
                        resolver.openOutputStream(imageUri)
                    } else null
                } else {
                    val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/DecorAR"
                    val file = File(imagesDir)
                    if (!file.exists()) file.mkdirs()
                    FileOutputStream(File(file, filename))
                }
                fos?.use {
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, it)
                } ?: run {
                    Toast.makeText(context, "No se pudo guardar la imagen", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        AlertDialog(
            onDismissRequest = { capturedBitmap = null },
            title = {
                Text(
                    text = "¿Desea guardar la captura?",
                    fontSize = 16.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Vista previa de captura",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        saveImageToGallery(bitmap)
                        capturedBitmap = null
                        showConfirmSavedModal = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E24AA)), // morado
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(text = "Confirmar", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { capturedBitmap = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)), // gris claro
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(text = "Cancelar", color = Color.Black)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
    if (showConfirmSavedModal) {
        ConfirmSpaceCreatedModal(
            onDismiss = { showConfirmSavedModal = false },
            onConfirm = {
                // Guardar la lista de modelos renderizados en SharedPreferences como JSON
                val modelsJson = JSONArray().apply {
                    viewModel.renderedModels.forEach { model ->
                        val obj = org.json.JSONObject()
                        obj.put("name", model.name)
                        obj.put("path", model.path)
                        put(obj)
                    }
                }.toString()
                sharedPrefHelper.saveStringData("furniture_list_json", modelsJson)
                showConfirmSavedModal = false
                navigateToCreateSpace()
            }
        )
    }


    if (haveAr) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            val lifecycleOwner = LocalLifecycleOwner.current
            //val engine = rememberEngine()
            val modelLoader = rememberModelLoader(engine)
            val materialLoader = rememberMaterialLoader(engine)
            val childNodes = rememberNodes()
            //val view = rememberView(engine)
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
                                        viewModel.sideDistance1.value = null
                                        viewModel.sideDistance2.value = null
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
                                viewModel.selectModelForDeletion(touchedModel)
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
                                        viewModel.selectModelForDeletion(null)
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
                        viewModel.clearCustomPlaneNodes(childNodes)
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
                    viewModel.clearCustomPlaneNodes(childNodes)
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
                    // Configurar el modo de profundidad si es compatible
                    config.depthMode =
                        if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) Config.DepthMode.AUTOMATIC
                        else Config.DepthMode.DISABLED

                    // Configurar el modo de colocación instantánea
                    config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP

                    // Configurar el modo de estimación de luz
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

            // NUEVO: Efecto para actualizar planos detectados y renderizar verticales
            LaunchedEffect(viewModel.planeRenderer.value, viewModel.session.value) {
                if (viewModel.planeRenderer.value) {
                    while (viewModel.planeRenderer.value) {
                        viewModel.updateDetectedPlanes(
                            session = viewModel.session.value,
                            engine = engine,
                            materialLoader = materialLoader,
                            childNodes = childNodes
                        )
                        kotlinx.coroutines.delay(1000) // Actualizar cada segundo
                    }
                } else {
                    // Si se desactiva el renderer, limpiar planos personalizados
                    viewModel.clearCustomPlaneNodes(childNodes)
                }
            }

            // NUEVO: Efecto para colocar baldosa automáticamente al entrar al modo baldosa
            LaunchedEffect(viewModel.isCoatingMode.value) {
                if (viewModel.isCoatingMode.value && viewModel.tileNode == null) {
                    Log.d(
                        "AR_DEBUG",
                        "Entrando a modo baldosa - buscando plano horizontal para colocar baldosa automáticamente"
                    )

                    // Intentar colocar baldosa automáticamente cada segundo hasta encontrar un plano
                    while (viewModel.isCoatingMode.value && viewModel.tileNode == null) {
                        val session = viewModel.session.value
                        val frame = viewModel.frame.value

                        if (session != null && frame != null) {
                            val autoTileNode = viewModel.tryAutoPlaceTile(
                                engine = engine,
                                modelLoader = modelLoader,
                                materialLoader = materialLoader,
                                session = session,
                                frame = frame
                            )

                            if (autoTileNode != null) {
                                childNodes += autoTileNode
                                Log.d("AR_DEBUG", "Baldosa colocada automáticamente")
                                break
                            }
                        }

                        kotlinx.coroutines.delay(1000) // Esperar 1 segundo antes de intentar de nuevo
                    }
                }
            }

            // Botón de back en la esquina superior izquierda
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 20.dp, top = 48.dp)
                    .zIndex(10f)
            ) {
                val interactionSourceBack = remember { MutableInteractionSource() }
                val isPressedBack by interactionSourceBack.collectIsPressedAsState()
                val scaleBack by animateFloatAsState(targetValue = if (isPressedBack) 0.92f else 1f, label = "scaleBack")
                
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                        .graphicsLayer(
                            scaleX = scaleBack,
                            scaleY = scaleBack
                        )
                        .clickable(
                            interactionSource = interactionSourceBack,
                            indication = null
                        ) {
                            navigateToTutorial()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val context = LocalContext.current
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("file:///android_asset/camara/back.svg")
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "Icono back",
                        modifier = Modifier
                            .width(70.dp)
                            .height(70.dp)
                    )
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, top = 16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                // Botón de fotos eliminado - ahora está debajo del selector de modelos
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 16.dp)
                    .zIndex(2f)
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    ModelSelector(viewModel = viewModel)
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
                    .padding(32.dp)
                    .padding(bottom = 70.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Espaciado para bajar el bloque de botones
                Spacer(modifier = Modifier.height(160.dp))
                // Animación de desplazamiento vertical para el botón de medición
                val offsetMedicion by animateDpAsState(
                    targetValue = if (viewModel.isMeasuring.value) (-80).dp else 0.dp,
                    label = "offsetMedicion"
                )
                // Animación de desplazamiento vertical para el botón de plano (ahora sube al activarse el modo medición)
                val offsetPlano by animateDpAsState(
                    targetValue = if (viewModel.isMeasuring.value) 0.dp else 80.dp,
                    label = "offsetPlano"
                )

                // Espaciado para subir el botón de plano
                Spacer(modifier = Modifier.height(32.dp))
                // Botón cuadrado de Plano (activar/desactivar plano)
                val interactionSourcePlano = remember { MutableInteractionSource() }
                val isPressedPlano by interactionSourcePlano.collectIsPressedAsState()
                val scalePlano by animateFloatAsState(targetValue = if (isPressedPlano) 0.92f else 1f, label = "scalePlano")
                val alphaPlano by animateFloatAsState(targetValue = if (viewModel.planeRenderer.value) 1f else 0.7f, label = "alphaPlano")
                Box(
                    modifier = Modifier
                        .width(65.dp)
                        .height(65.dp)
                        .offset(x = 0.dp, y = -55.dp + offsetPlano)
                        .zIndex(2f)
                        .graphicsLayer(
                            scaleX = scalePlano,
                            scaleY = scalePlano,
                            alpha = alphaPlano
                        )
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(size = 20.dp)
                        )
                        .clickable(
                            interactionSource = interactionSourcePlano,
                            indication = null
                        ) {
                            viewModel.planeRenderer.value = !viewModel.planeRenderer.value
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val context = LocalContext.current
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(
                                if (viewModel.planeRenderer.value)
                                    "file:///android_asset/camara/bg-button-on.svg"
                                else
                                    "file:///android_asset/camara/bg-button-red.svg"
                            )
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "Fondo botón plano",
                        modifier = Modifier
                            .width(65.dp)
                            .height(65.dp),
                        contentScale = ContentScale.FillBounds
                    )
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("file:///android_asset/camara/flat.svg")
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "Icono plano",
                        modifier = Modifier
                            .width(55.dp)
                            .height(55.dp)
                            .padding(vertical = 5.dp)
                    )
                }

                // Stack animado: botón de medición y área (sin offset vertical extra)
                Box(
                    modifier = Modifier
                        .height(145.dp)
                        .width(65.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Botón de área (detrás)
                    val showArea = (viewModel.isMeasuring.value || offsetMedicion < 0.dp)
                    if (showArea) {
                        val interactionSourceArea = remember { MutableInteractionSource() }
                        val isPressedArea by interactionSourceArea.collectIsPressedAsState()
                        val scaleArea by animateFloatAsState(targetValue = if (isPressedArea) 0.92f else 1f, label = "scaleArea")
                        val alphaArea by animateFloatAsState(targetValue = if (viewModel.isCalculatingArea.value) 1f else 0.7f, label = "alphaArea")
                        Box(
                            modifier = Modifier
                                .width(65.dp)
                                .height(65.dp)
                                .zIndex(0f)
                                .graphicsLayer(
                                    scaleX = scaleArea,
                                    scaleY = scaleArea,
                                    alpha = alphaArea
                                )
                                .background(
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(size = 20.dp)
                                )
                                .clickable(
                                    interactionSource = interactionSourceArea,
                                    indication = null
                                ) {
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
                            contentAlignment = Alignment.Center,
                        ) {
                            val context = LocalContext.current
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(
                                        if (viewModel.isCalculatingArea.value)
                                            "file:///android_asset/camara/bg-button-on.svg"
                                        else
                                            "file:///android_asset/camara/bg-button-off.svg"
                                    )
                                    .decoderFactory(SvgDecoder.Factory())
                                    .build(),
                                contentDescription = "Fondo botón área",
                                modifier = Modifier
                                    .width(65.dp)
                                    .height(65.dp),
                                contentScale = ContentScale.FillBounds
                            )
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data("file:///android_asset/camara/area.svg")
                                    .decoderFactory(SvgDecoder.Factory())
                                    .build(),
                                contentDescription = "Icono área",
                                modifier = Modifier
                                    .width(55.dp)
                                    .height(55.dp)
                                    .padding(vertical = 5.dp)
                            )
                        }
                    }

                    // Botón cuadrado de Medición con animación
                    val interactionSourceMedicion = remember { MutableInteractionSource() }
                    val isPressedMedicion by interactionSourceMedicion.collectIsPressedAsState()
                    val scaleMedicion by animateFloatAsState(targetValue = if (isPressedMedicion) 0.92f else 1f, label = "scaleMedicion")
                    val alphaMedicion by animateFloatAsState(targetValue = if (viewModel.isMeasuring.value) 1f else 0.7f, label = "alphaMedicion")
                    Box(
                        modifier = Modifier
                            .width(65.dp)
                            .height(65.dp)
                            .offset(y = offsetMedicion)
                            .zIndex(1f)
                            .graphicsLayer(
                                scaleX = scaleMedicion,
                                scaleY = scaleMedicion,
                                alpha = alphaMedicion
                            )
                            .background(
                                color = Color.Transparent,
                                shape = RoundedCornerShape(size = 20.dp)
                            )
                            .clickable(
                                interactionSource = interactionSourceMedicion,
                                indication = null
                            ) {
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
                        contentAlignment = Alignment.Center
                    ) {
                        val context = LocalContext.current
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(
                                    if (offsetMedicion == 0.dp)
                                        "file:///android_asset/camara/bg-button-off.svg"
                                    else
                                        "file:///android_asset/camara/bg-button-on.svg"
                                )
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "Fondo botón medición",
                            modifier = Modifier
                                .width(65.dp)
                                .height(65.dp),
                            contentScale = ContentScale.FillBounds
                        )
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data("file:///android_asset/camara/measure-ruler.svg")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "Regla de medición",
                            modifier = Modifier
                                .width(55.dp)
                                .height(55.dp)
                                .padding(vertical = 5.dp)
                        )
                    }
                }

                // Botón cuadrado de Modo Baldosa (estático, decorado igual que los otros)
                val interactionSourceBaldosa = remember { MutableInteractionSource() }
                val isPressedBaldosa by interactionSourceBaldosa.collectIsPressedAsState()
                val scaleBaldosa by animateFloatAsState(targetValue = if (isPressedBaldosa) 0.92f else 1f, label = "scaleBaldosa")
                val alphaBaldosa by animateFloatAsState(targetValue = if (viewModel.isCoatingMode.value) 1f else 0.7f, label = "alphaBaldosa")
                Box(
                    modifier = Modifier
                        .width(65.dp)
                        .height(65.dp)
                        .offset(x = 0.dp, y = -25.dp)
                        .graphicsLayer(
                            scaleX = scaleBaldosa,
                            scaleY = scaleBaldosa,
                            alpha = alphaBaldosa
                        )
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(size = 20.dp)
                        )
                        .clickable(
                            interactionSource = interactionSourceBaldosa,
                            indication = null
                        ) {
                        viewModel.isCoatingMode.value = !viewModel.isCoatingMode.value
                        if (!viewModel.isCoatingMode.value) {
                            if (viewModel.tileNode != null) {
                                childNodes.remove(viewModel.tileNode)
                                (viewModel.tileNode as? AnchorNode)?.anchor?.detach()
                                viewModel.resetTileNode()
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
                    contentAlignment = Alignment.Center
                ) {
                    val context = LocalContext.current
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(
                                if (viewModel.isCoatingMode.value)
                                    "file:///android_asset/camara/bg-button-on.svg"
                                else
                                    "file:///android_asset/camara/bg-button-off.svg"
                            )
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "Fondo botón baldosa",
                        modifier = Modifier
                            .width(65.dp)
                            .height(65.dp),
                        contentScale = ContentScale.FillBounds
                    )
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("file:///android_asset/camara/tile.svg")
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "Icono baldosa",
                        modifier = Modifier
                            .width(55.dp)
                            .height(55.dp)
                            .padding(vertical = 5.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 15.dp, bottom = 120.dp) // Más arriba y más visible
            ) {
                // Botón de captura tipo cámara
                val interactionSourceCapture = remember { MutableInteractionSource() }
                val isPressedCapture by interactionSourceCapture.collectIsPressedAsState()
                val scaleCapture by animateFloatAsState(targetValue = if (isPressedCapture) 0.92f else 1f, label = "scaleCapture")
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                        .graphicsLayer(
                            scaleX = scaleCapture,
                            scaleY = scaleCapture
                        )
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(size = 20.dp)
                        )
                        .clickable(
                            interactionSource = interactionSourceCapture,
                            indication = null
                        ) {
                            takeScreenshot()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val context = LocalContext.current
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("file:///android_asset/camara/capture.svg")
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "Botón capturar foto",
                        modifier = Modifier
                            .width(80.dp)
                            .height(80.dp)
                            .padding(vertical = 5.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 210.dp)
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

            // NUEVO: Modal de confirmación para eliminar modelo
            viewModel.selectedPlacedModel.value?.let { selectedModel ->
                AlertDialog(
                    onDismissRequest = { viewModel.cancelModelDeletion() },
                    title = { Text("Eliminar Modelo") },
                    text = { Text("¿Estás seguro de que quieres eliminar este modelo?") },
                    confirmButton = {
                        TextButton(
                            onClick = { viewModel.confirmModelDeletion(childNodes) }
                        ) {
                            Text("Eliminar", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { viewModel.cancelModelDeletion() }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            // Animación de la NavBar deslizándose hacia abajo
            var startNavBarAnimation by remember { mutableStateOf(false) }
            val navBarOffset by animateDpAsState(
                targetValue = if (startNavBarAnimation) 200.dp else 0.dp, // Se desliza hacia abajo y desaparece
                animationSpec = androidx.compose.animation.core.tween(durationMillis = 800),
                label = "navBarOffset"
            )
            
            // Iniciar la animación cuando se carga la pantalla
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(500) // Pequeña pausa antes de animar
                startNavBarAnimation = true
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .offset(y = navBarOffset)
                        .zIndex(1f),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    NavBar(
                        toCamera = null,
                        toTutorial = navigateToTutorial,
                        toCatalog = navigateToCatalog,
                        toSpaces = navigateToSpaces,
                        toConfiguration = navigateToConfiguration,
                    )
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

    // Nuevo: Botón para mostrar lista de modelos renderizados
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .zIndex(2f)
    ) {
        FloatingActionButton(
            onClick = { viewModel.toggleModelsList() },
            modifier = Modifier
                .padding(end = 16.dp)
                .size(56.dp)
                .align(Alignment.TopEnd),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Badge(
                modifier = Modifier.offset(x = 14.dp, y = (-14).dp)
            ) {
                Text(text = viewModel.renderedModels.size.toString())
            }
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/camara/list.svg")
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = "Lista de modelos",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // Nuevo: Diálogo para mostrar la lista de modelos
    if (viewModel.showModelsList.value) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleModelsList() },
            title = {
                Text(
                    text = "Modelos en Escena",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                if (viewModel.renderedModels.isEmpty()) {
                    Text(
                        text = "No hay modelos en escena",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyColumn {
                        items(viewModel.renderedModels) { model ->
                            ListItem(
                                headlineContent = { 
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(model.name)
                                        Text(
                                            text = "x${model.count}",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                supportingContent = { Text(model.path) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.toggleModelsList() }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
fun ConfirmSpaceCreatedModal(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit // 🚩 NUEVO
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "¿Desea crear un espacio?",
                fontSize = 18.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm() // 🚩 Ejecutar navegación a CreateSpaceScreen
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E24AA)), // morado
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .defaultMinSize(minWidth = 100.dp)
            ) {
                Text(text = "Confirmar", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)), // gris claro
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .defaultMinSize(minWidth = 100.dp)
            ) {
                Text(text = "Cancelar", color = Color.Black)
            }
        }
    )
}

// Función para guardar la lista de modelos como JSON
private fun saveModelsListToJson(
    context: Context,
    models: List<RenderedModelInfo>,
    timestamp: Long
) {
    try {
        val directory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DecorAR")
        } else {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/DecorAR")
        }

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val jsonFile = File(directory, "models_$timestamp.json")
        val jsonContent = buildString {
            append("{\n")
            append("  \"timestamp\": \"$timestamp\",\n")
            append("  \"models\": [\n")
            models.forEachIndexed { index, model ->
                append("    {\n")
                append("      \"name\": \"${model.name}\",\n")
                append("      \"type\": \"${model.name.split(" ").lastOrNull() ?: model.name}\"\n")
                append("    }")
                if (index < models.size - 1) append(",")
                append("\n")
            }
            append("  ]\n")
            append("}")
        }

        jsonFile.writeText(jsonContent)
        Log.d("CameraScreen", "Lista de modelos guardada en: ${jsonFile.absolutePath}")
    } catch (e: Exception) {
        Log.e("CameraScreen", "Error al guardar la lista de modelos: ${e.message}")
    }
}

// Modificar la función de captura existente
private fun captureAndSaveImage(
    context: Context,
    bitmap: Bitmap,
    viewModel: CameraViewModel,
    onSuccess: () -> Unit
) {
    try {
        val timestamp = System.currentTimeMillis()
        val directory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DecorAR")
        } else {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/DecorAR")
        }

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val imageFile = File(directory, "space_$timestamp.jpg")
        val fos = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()

        // Guardar la lista de modelos
        saveModelsListToJson(context, viewModel.renderedModels, timestamp)

        // Notificar al sistema de archivos usando MediaStore (método moderno)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "space_$timestamp.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DecorAR")
            }
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            // Para versiones anteriores, usar MediaScanner
            MediaScannerConnection.scanFile(
                context,
                arrayOf(imageFile.absolutePath),
                arrayOf("image/jpeg")
            ) { path, uri -> 
                Log.d("CameraScreen", "Escaneo completado: $path")
            }
        }

        onSuccess()
    } catch (e: Exception) {
        Log.e("CameraScreen", "Error al guardar la imagen: ${e.message}")
    }
}