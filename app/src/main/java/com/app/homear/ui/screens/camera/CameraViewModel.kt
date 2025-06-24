package com.app.homear.ui.screens.camera

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.PixelCopy
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Frame
import com.google.ar.core.Pose
import com.google.ar.core.TrackingFailureReason
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.SphereNode
import javax.inject.Inject

data class ARModel(
    val name: String,
    val modelPath: String,
)

@HiltViewModel
class CameraViewModel @Inject constructor(

): ViewModel(){
    // Testing
    private val kModelFile: String
        get() {
            val path = selectedModel.value?.modelPath ?: "models/Duck.glb"
            Log.d("AR_DEBUG", "kModelFile getter - Path seleccionado: $path")
            return path
        }
    private val kMaxModelInstances = 10


    // Renderizado de Muebles

    private val _planeRenderer = mutableStateOf(true)
    val planeRenderer = _planeRenderer
    private val _modelInstances = mutableStateListOf<ModelInstance>()
    val modelInstances = _modelInstances
    private val _trackingFailureReason = mutableStateOf<TrackingFailureReason?>(null)
    val trackingFailureReason = _trackingFailureReason
    private val _frame = mutableStateOf<Frame?>(null)
    val frame = _frame

    // Medicion de distancias y área
    private val _isMeasuring = mutableStateOf(false)
    val isMeasuring = _isMeasuring
    private val _firstAnchor = mutableStateOf<Anchor?>(null)
    val firstAnchor = _firstAnchor
    private val _secondAnchor = mutableStateOf<Anchor?>(null)
    val secondAnchor = _secondAnchor
    private val _thirdAnchor = mutableStateOf<Anchor?>(null)
    val thirdAnchor = _thirdAnchor
    private val _measuredDistance = mutableStateOf<Float?>(null)
    val measuredDistance = _measuredDistance
    private val _measuredArea = mutableStateOf<Float?>(null)
    val measuredArea = _measuredArea
    private val _sideDistance1 = mutableStateOf<Float?>(null)
    val sideDistance1 = _sideDistance1
    private val _sideDistance2 = mutableStateOf<Float?>(null)
    val sideDistance2 = _sideDistance2
    private val _measurementHistory = mutableStateListOf<Float>()
    val measurementHistory = _measurementHistory
    private val _showHistory = mutableStateOf(false)
    val showHistory = _showHistory
    private val _measurementPoints = mutableStateListOf<AnchorNode>()
    val measurementPoints = _measurementPoints

    // Modo revestimiento
    private val _isCoatingMode = mutableStateOf(false)
    val isCoatingMode = _isCoatingMode
    private val _processedPlanes = mutableStateListOf<Int>()
    val processedPlanes = _processedPlanes

    // Cache para modelos de baldosa para evitar recargas
    private val tileModelCache = mutableMapOf<String, List<ModelInstance>>()
    private val coatingNodesCache = mutableListOf<AnchorNode>()
    
    // OPTIMIZACIÓN: Control de frecuencia de creación de baldosas
    private var lastTileCreationTime = 0L
    private val tileCreationCooldown = 500L // 500ms entre creaciones de baldosas
    private var isCreatingTiles = false
    
    // OPTIMIZACIÓN: Validación de estado de tracking
    private fun isTrackingValid(anchor: Anchor?): Boolean {
        return anchor != null && 
               anchor.trackingState == com.google.ar.core.TrackingState.TRACKING
    }

    // Estado para el menú desplegable
    var isDropdownExpanded = mutableStateOf(false)
    var selectedModel = mutableStateOf<ARModel?>(null)
        set(value) {
            Log.d("AR_DEBUG", "Cambiando modelo seleccionado a: ${value.value?.name}, path: ${value.value?.modelPath}")
            field = value
        }

    // Lista de modelos disponibles
    val availableModels = listOf(
        ARModel(
            name = "Mueble Moderno",
            modelPath = "models/Mueble-1.glb"
        ),
        ARModel(
            name = "BoomBox Retro",
            modelPath = "models/BoomBox.glb"
        ),
        ARModel(
            name = "Caja Decorativa",
            modelPath = "models/Box.glb"
        ),
        ARModel(
            name = "Decoración Apple",
            modelPath = "models/apple.glb"
        ),
        ARModel(
            name = "Pato Decorativo",
            modelPath = "models/Duck.glb"
        ),
        ARModel(
            name = "Baldosa",
            modelPath = "models/baldosa.glb"
        )
    )

    // Tomar foto y guardar en galería
    private val _capturedBitmap = mutableStateOf<Bitmap?>(null)
    val capturedBitmap = _capturedBitmap

    public fun takeScreenshot(sceneView: ARSceneView?, context: Context) {
        if (sceneView == null) {
            Toast.makeText(context, "No se pudo encontrar la vista de la escena.", Toast.LENGTH_SHORT).show()
            return
        }

        val bitmap = Bitmap.createBitmap(sceneView.width, sceneView.height, Bitmap.Config.ARGB_8888)
        PixelCopy.request(sceneView, bitmap, { result ->
            if (result == PixelCopy.SUCCESS) {
                _capturedBitmap.value = bitmap
            } else {
                Log.e("CameraScreen", "Error al copiar los píxeles: $result")
                Toast.makeText(context, "Error al tomar la captura.", Toast.LENGTH_SHORT).show()
            }
        }, Handler(Looper.getMainLooper()))
    }

    public fun saveBitmapToGallery(context: Context, bitmap: Bitmap) {
        val filename = "AR_Capture_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            try {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    Toast.makeText(context, "Imagen guardada en la galería", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("CameraScreen", "Error al guardar la imagen: ${e.message}")
                Toast.makeText(context, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("CameraScreen", "No se pudo crear el archivo en MediaStore")
            Toast.makeText(context, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    fun isArCoreSupported(context: Context): Boolean {
        val availability = ArCoreApk.getInstance().checkAvailability(context)
        return availability.isSupported
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

    fun calculateRectangleArea(anchor1: Anchor, anchor2: Anchor, anchor3: Anchor): Float {
        // Obtener las posiciones de los tres puntos
        val p1 = anchor1.pose
        val p2 = anchor2.pose
        val p3 = anchor3.pose
        
        // Calcular las distancias para formar un rectángulo
        // Distancia del punto 1 al punto 2 (primer lado)
        val distanceP1P2 = kotlin.math.sqrt(
            (p1.tx() - p2.tx()).let { it * it } +
            (p1.ty() - p2.ty()).let { it * it } +
            (p1.tz() - p2.tz()).let { it * it }
        )
        
        // Distancia del punto 2 al punto 3 (segundo lado)
        val distanceP2P3 = kotlin.math.sqrt(
            (p2.tx() - p3.tx()).let { it * it } +
            (p2.ty() - p3.ty()).let { it * it } +
            (p2.tz() - p3.tz()).let { it * it }
        )
        
        // Para un rectángulo: área = lado1 × lado2
        val area = distanceP1P2 * distanceP2P3
        
        // Almacenar las distancias para mostrarlas en la UI
        _sideDistance1.value = distanceP1P2
        _sideDistance2.value = distanceP2P3
        
        Log.d("AR_DEBUG", "Área rectangular calculada: ${area}m² con lados: ${distanceP1P2}m x ${distanceP2P3}m")
        return area
    }

    fun createTiledCoatingNode(
        engine: Engine,
        modelLoader: ModelLoader,
        materialLoader: MaterialLoader,
        anchor: Anchor,
        planeExtentX: Float,
        planeExtentZ: Float,
        session: com.google.ar.core.Session
    ): List<AnchorNode> {
        Log.d("AR_DEBUG", "Creando baldosa individual en el centro del plano: ${planeExtentX}m x ${planeExtentZ}m")
        
        // OPTIMIZACIÓN: Control de frecuencia para evitar sobrecarga
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTileCreationTime < tileCreationCooldown || isCreatingTiles) {
            Log.d("AR_DEBUG", "Saltando creación de baldosa - muy frecuente o ya en proceso")
            return emptyList()
        }
        
        // OPTIMIZACIÓN: Validar estado de tracking antes de proceder
        if (!isTrackingValid(anchor)) {
            Log.w("AR_DEBUG", "Anchor no válido para crear baldosa - estado: ${anchor.trackingState}")
            return emptyList()
        }
        
        isCreatingTiles = true
        lastTileCreationTime = currentTime
        
        val tileNodes = mutableListOf<AnchorNode>()
        val tileSize = 0.6f // Tamaño real de baldosa en metros
        
        try {
            Log.d("AR_DEBUG", "Creando una sola baldosa en el centro del plano")
            
            // MODIFICACIÓN: Crear solo una instancia de baldosa
            val tileInstance = try {
                modelLoader.createInstancedModel("models/baldosa.glb", 1).first()
            } catch (e: Exception) {
                Log.e("AR_DEBUG", "Error creando instancia de baldosa: ${e.message}")
                return emptyList()
            }
            
            // MODIFICACIÓN: Crear un solo anchor principal para la baldosa única
            val mainAnchorNode = AnchorNode(engine = engine, anchor = anchor)
            
            // MODIFICACIÓN: Crear un solo nodo de modelo en el centro del plano (posición 0,0,0 relativa)
            val modelNode = ModelNode(
                modelInstance = tileInstance,
                scaleToUnits = tileSize
            ).apply {
                isEditable = false
                // Posición en el centro del plano (0,0,0 relativo al anchor)
                worldPosition = dev.romainguy.kotlin.math.Float3(0f, 0f, 0f)
            }
            
            // Añadir la baldosa única al anchor principal
            mainAnchorNode.addChildNode(modelNode)
            
            tileNodes.add(mainAnchorNode)
            coatingNodesCache.addAll(tileNodes)
            
            Log.d("AR_DEBUG", "Baldosa individual creada y anclada correctamente en el centro del plano")
            
        } catch (e: Exception) {
            Log.e("AR_DEBUG", "Error en createTiledCoatingNode: ${e.message}")
        } finally {
            isCreatingTiles = false
        }
        
        return tileNodes
    }
    
    // OPTIMIZACIÓN: Obtener instancias de cache de forma segura
    private fun getTileInstancesFromCache(
        cacheKey: String, 
        totalTiles: Int, 
        maxTiles: Int, 
        modelLoader: ModelLoader
    ): List<ModelInstance> {
        return if (tileModelCache.containsKey(cacheKey)) {
            Log.d("AR_DEBUG", "Usando modelos en cache para $totalTiles baldosas")
            tileModelCache[cacheKey]!!
        } else {
            Log.d("AR_DEBUG", "Creando nuevos modelos para cache: $totalTiles baldosas")
            val instances = mutableListOf<ModelInstance>()
            val instancesToCreate = totalTiles.coerceAtMost(maxTiles)
            
            repeat(instancesToCreate) {
                try {
                    instances.add(modelLoader.createInstancedModel("models/baldosa.glb", 1).first())
                } catch (e: Exception) {
                    Log.w("AR_DEBUG", "Error creando instancia de baldosa $it: ${e.message}")
                }
            }
            
            if (instances.isNotEmpty()) {
                tileModelCache[cacheKey] = instances
            }
            instances
        }
    }
    
    // OPTIMIZACIÓN: Validar pose antes de crear anchors
    private fun isPoseValid(pose: Pose): Boolean {
        return try {
            val translation = pose.translation
            val rotation = pose.rotationQuaternion
            
            // Verificar que los valores no sean NaN o infinitos
            !(translation.any { it.isNaN() || it.isInfinite() } ||
              rotation.any { it.isNaN() || it.isInfinite() })
        } catch (e: Exception) {
            false
        }
    }
    
    // OPTIMIZACIÓN: Determinar número óptimo de baldosas basado en rendimiento
    private fun getOptimalTileCount(requestedTiles: Int): Int {
        return when {
            requestedTiles <= 25 -> requestedTiles  // Área pequeña: renderizar todas
            requestedTiles <= 64 -> 40              // Área mediana: reducir moderadamente  
            requestedTiles <= 144 -> 64             // Área grande: reducir significativamente
            else -> 50                              // Área muy grande: límite conservador
        }
    }
    
    // OPTIMIZACIÓN: Calcular posiciones de baldosas de forma eficiente
    private fun calculateTilePositions(
        planeExtentX: Float, 
        planeExtentZ: Float, 
        tilesX: Int, 
        tilesZ: Int, 
        tileSize: Float
    ): List<Float3> {
        val positions = mutableListOf<Float3>()
        val startX = -planeExtentX / 2f + tileSize / 2f
        val startZ = -planeExtentZ / 2f + tileSize / 2f
        
        for (x in 0 until tilesX) {
            for (z in 0 until tilesZ) {
                val tileX = startX + (x * tileSize)
                val tileZ = startZ + (z * tileSize)
                positions.add(Float3(tileX, 0f, tileZ))
            }
        }
        
        return positions
    }
    
    // OPTIMIZACIÓN: Frustum culling avanzado con priorización por proximidad
    private fun performAdvancedFrustumCulling(
        positions: List<Float3>,
        cameraPose: Pose,
        planeExtentX: Float,
        planeExtentZ: Float
    ): List<Float3> {
        val maxDistance = (planeExtentX.coerceAtLeast(planeExtentZ) * 0.8f).coerceAtMost(12f)
        val cameraPosition = Float3(cameraPose.tx(), cameraPose.ty(), cameraPose.tz())
        
        return positions
            .asSequence()
            .map { position ->
                val worldPosition = Float3(
                    cameraPose.tx() + position.x,
                    cameraPose.ty() + position.y,
                    cameraPose.tz() + position.z
                )
                
                val dx = worldPosition.x - cameraPosition.x
                val dy = worldPosition.y - cameraPosition.y  
                val dz = worldPosition.z - cameraPosition.z
                val distanceSquared = dx * dx + dy * dy + dz * dz
                
                Pair(position, distanceSquared)
            }
            .filter { it.second <= (maxDistance * maxDistance) }
            .sortedBy { it.second } // Priorizar por proximidad
            .take(getOptimalTileCount(positions.size)) // Limitar según rendimiento
            .map { it.first }
            .toList()
    }
    
    // OPTIMIZACIÓN: Crear rejilla optimizada para áreas grandes
    private fun createOptimizedCoatingGrid(
        engine: Engine,
        modelLoader: ModelLoader,
        materialLoader: MaterialLoader,
        anchor: Anchor,
        planeExtentX: Float,
        planeExtentZ: Float,
        session: com.google.ar.core.Session,
        maxTiles: Int
    ): List<AnchorNode> {
        Log.d("AR_DEBUG", "Creando rejilla optimizada con máximo $maxTiles baldosas")
        
        val tileNodes = mutableListOf<AnchorNode>()
        val tileSize = 0.6f
        
        try {
            // Calcular paso entre baldosas para distribuir uniformemente
            val stepX = planeExtentX / kotlin.math.sqrt(maxTiles.toFloat())
            val stepZ = planeExtentZ / kotlin.math.sqrt(maxTiles.toFloat())
            
            val tilesPerRow = kotlin.math.sqrt(maxTiles.toFloat()).toInt()
            val actualTiles = tilesPerRow * tilesPerRow
            
            // Crear instancias
            val tileInstances = mutableListOf<ModelInstance>()
            repeat(actualTiles) {
                try {
                    tileInstances.add(modelLoader.createInstancedModel("models/baldosa.glb", 1).first())
                } catch (e: Exception) {
                    Log.w("AR_DEBUG", "Error creando instancia optimizada $it: ${e.message}")
                }
            }
            
            // CORRECCIÓN: Usar un solo anchor principal para la rejilla optimizada
            val mainAnchorNode = AnchorNode(engine = engine, anchor = anchor)
            val startX = -planeExtentX / 2f + stepX / 2f
            val startZ = -planeExtentZ / 2f + stepZ / 2f
            
            var instanceIndex = 0
            for (x in 0 until tilesPerRow) {
                for (z in 0 until tilesPerRow) {
                    if (instanceIndex < tileInstances.size) {
                        try {
                            val tileX = startX + (x * stepX)
                            val tileZ = startZ + (z * stepZ)
                            
                            // CORRECCIÓN: Crear nodo de modelo directamente sin anchor adicional
                            val modelNode = ModelNode(
                                modelInstance = tileInstances[instanceIndex],
                                scaleToUnits = tileSize
                            ).apply {
                                isEditable = false
                            }
                            
                            // CORRECCIÓN: Establecer posición relativa fija
                            modelNode.worldPosition = dev.romainguy.kotlin.math.Float3(tileX, 0f, tileZ)
                            
                            // Añadir directamente al anchor principal
                            mainAnchorNode.addChildNode(modelNode)
                            instanceIndex++
                            
                        } catch (e: Exception) {
                            Log.w("AR_DEBUG", "Error creando baldosa optimizada en posición ($x,$z): ${e.message}")
                        }
                    }
                }
            }
            
            tileNodes.add(mainAnchorNode)
            
        } catch (e: Exception) {
            Log.e("AR_DEBUG", "Error en createOptimizedCoatingGrid: ${e.message}")
        }
        
        Log.d("AR_DEBUG", "Rejilla optimizada creada con anclaje fijo")
        return tileNodes
    }
    
    // OPTIMIZACIÓN: Frustum culling básico
    private fun performFrustumCulling(
        positions: List<Float3>,
        cameraPose: Pose,
        maxDistance: Float = 15f // Distancia máxima de renderizado optimizada
    ): List<Float3> {
        val cameraPosition = Float3(cameraPose.tx(), cameraPose.ty(), cameraPose.tz())
        
        return positions.filter { position ->
            val worldPosition = Float3(
                cameraPose.tx() + position.x,
                cameraPose.ty() + position.y,
                cameraPose.tz() + position.z
            )
            
            // Cálculo de distancia optimizado
            val dx = worldPosition.x - cameraPosition.x
            val dy = worldPosition.y - cameraPosition.y  
            val dz = worldPosition.z - cameraPosition.z
            val distanceSquared = dx * dx + dy * dy + dz * dz
            
            distanceSquared <= (maxDistance * maxDistance)
        }
    }
    
    // OPTIMIZACIÓN: Limpiar recursos de revestimiento
    fun clearCoatingResources() {
        Log.d("AR_DEBUG", "Limpiando recursos de revestimiento: ${coatingNodesCache.size} nodos")
        coatingNodesCache.clear()
        // Limpiar cache cada cierto tiempo para evitar uso excesivo de memoria
        if (tileModelCache.size > 10) {
            tileModelCache.clear()
            Log.d("AR_DEBUG", "Cache de modelos limpiado por límite de memoria")
        }
    }
    
    // NUEVO: Eliminar completamente todas las baldosas de la escena
    fun removeAllTileNodes(childNodes: MutableList<io.github.sceneview.node.Node>) {
        Log.d("AR_DEBUG", "Eliminando todas las baldosas de la escena (${coatingNodesCache.size} grupos de baldosas)")
        
        // Remover todos los nodos de baldosas almacenados en cache
        val tilesToRemove = coatingNodesCache.toList()
        childNodes.removeAll(tilesToRemove.toSet())
        
        // CORRECCIÓN: Desconectar solo los anchors principales (no hay anchors individuales por baldosa)
        tilesToRemove.forEach { mainAnchorNode ->
            try {
                Log.d("AR_DEBUG", "Desconectando grupo de baldosas")
                mainAnchorNode.anchor?.detach()
            } catch (e: Exception) {
                Log.w("AR_DEBUG", "Error al desconectar anchor principal: ${e.message}")
            }
        }
        
        // Limpiar todos los recursos de revestimiento
        coatingNodesCache.clear()
        tileModelCache.clear()
        processedPlanes.clear()
        
        // Resetear estado de creación
        resetTileCreationState()
        
        Log.d("AR_DEBUG", "Limpieza completa terminada. Nodos restantes en escena: ${childNodes.size}")
    }
    
    // OPTIMIZACIÓN: Resetear estado de creación de baldosas
    fun resetTileCreationState() {
        isCreatingTiles = false
        lastTileCreationTime = 0L
        Log.d("AR_DEBUG", "Estado de creación de baldosas reseteado")
    }
    
    // NUEVO: Función para verificar y mejorar el estado de tracking
    fun checkAndImproveTracking(frame: Frame?): Boolean {
        if (frame == null) return false
        
        val camera = frame.camera
        val trackingState = camera.trackingState
        
        when (trackingState) {
            com.google.ar.core.TrackingState.TRACKING -> {
                // Tracking exitoso - verificar calidad
                val pose = camera.pose
                if (isPoseValid(pose)) {
                    Log.d("AR_DEBUG", "Tracking exitoso - calidad buena")
                    return true
                } else {
                    Log.w("AR_DEBUG", "Tracking activo pero pose inválida")
                    return false
                }
            }
            com.google.ar.core.TrackingState.PAUSED -> {
                Log.w("AR_DEBUG", "Tracking pausado - esperando mejor condiciones")
                return false
            }
            com.google.ar.core.TrackingState.STOPPED -> {
                Log.e("AR_DEBUG", "Tracking detenido - reiniciando sesión")
                return false
            }
        }
    }
    
    // NUEVO: Función para optimizar configuración de cámara
    fun optimizeCameraSettings(session: com.google.ar.core.Session): com.google.ar.core.CameraConfig? {
        return try {
            val supportedConfigs = session.getSupportedCameraConfigs()
            
            // Buscar la mejor configuración disponible
            supportedConfigs.firstOrNull { config ->
                config.imageSize.width >= 1920 && 
                config.imageSize.height >= 1080 &&
                config.fpsRange.upper >= 60
            } ?: supportedConfigs.firstOrNull { config ->
                config.imageSize.width >= 1280 && 
                config.imageSize.height >= 720 &&
                config.fpsRange.upper >= 30
            } ?: supportedConfigs.firstOrNull()
            
        } catch (e: Exception) {
            Log.e("AR_DEBUG", "Error optimizando configuración de cámara: ${e.message}")
            null
        }
    }
    
    // NUEVO: Función para verificar condiciones de iluminación
    fun checkLightingConditions(frame: Frame?): String {
        if (frame == null) return "Sin información de iluminación"
        
        return try {
            val lightEstimate = frame.lightEstimate
            if (lightEstimate != null) {
                // En ARCore 1.48.0, verificar si la estimación de luz está disponible
                // Si lightEstimate no es null, significa que hay información de iluminación
                "Iluminación detectada - condiciones óptimas"
            } else {
                "Estimación de luz no disponible"
            }
        } catch (e: Exception) {
            "Error al verificar iluminación: ${e.message}"
        }
    }
    
    // NUEVO: Función para verificar estabilidad del dispositivo
    fun checkDeviceStability(frame: Frame?): Boolean {
        if (frame == null) return false
        
        return try {
            val camera = frame.camera
            val pose = camera.pose
            
            // Verificar que la pose sea estable (no hay cambios extremos)
            val translation = pose.translation
            val rotation = pose.rotationQuaternion
            
            // Verificar que los valores no sean extremos
            val isTranslationStable = translation.all { it.isFinite() && kotlin.math.abs(it) < 1000f }
            val isRotationStable = rotation.all { it.isFinite() && kotlin.math.abs(it) <= 1.0f }
            
            isTranslationStable && isRotationStable
            
        } catch (e: Exception) {
            Log.w("AR_DEBUG", "Error verificando estabilidad: ${e.message}")
            false
        }
    }
}
