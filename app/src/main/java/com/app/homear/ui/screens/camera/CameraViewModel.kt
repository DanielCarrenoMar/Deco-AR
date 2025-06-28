package com.app.homear.ui.screens.camera

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Frame
import com.google.ar.core.Pose
import com.google.ar.core.Plane
import com.google.ar.core.Session
import com.google.ar.core.TrackingFailureReason
import com.google.ar.core.TrackingState
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.node.SphereNode
import javax.inject.Inject

data class ARModel(
    val name: String,
    val modelPath: String,
)

// NUEVO: Clase para representar un modelo colocado en AR con información de selección
data class PlacedARModel(
    val anchorNode: AnchorNode,
    val modelNode: ModelNode,
    val boundingBoxNode: CubeNode,
    val id: String = java.util.UUID.randomUUID().toString()
)

@HiltViewModel
class CameraViewModel @Inject constructor(

): ViewModel(){
    // Testing
    private val kModelFile: String
        get() {
            val path = selectedModel.value?.modelPath ?: "models/Mueble-1.glb"
            Log.d("AR_DEBUG", "kModelFile getter - Path seleccionado: $path")
            return path
        }
    private val kMaxModelInstances = 10

    // NUEVO: Dimensiones de pantalla para proyección precisa
    var screenWidth = 800f
        private set
    var screenHeight = 600f
        private set

    // Renderizado de Muebles

    private val _planeRenderer = mutableStateOf(true)
    val planeRenderer = _planeRenderer

    // MEJORADO: Usar un mapa de instancias por modelo para evitar conflictos
    private val _modelInstancesMap = mutableMapOf<String, MutableList<ModelInstance>>()
    private val _modelInstances = mutableStateListOf<ModelInstance>()
    val modelInstances = _modelInstances

    private val _trackingFailureReason = mutableStateOf<TrackingFailureReason?>(null)
    val trackingFailureReason = _trackingFailureReason
    private val _frame = mutableStateOf<Frame?>(null)
    val frame = _frame
    private val _session = mutableStateOf<Session?>(null)
    val session = _session

    // Medicion de distancias y área
    private val _isMeasuring = mutableStateOf(false)
    val isMeasuring = _isMeasuring
    private val _isCalculatingArea = mutableStateOf(false)
    val isCalculatingArea = _isCalculatingArea
    private val _firstAnchor = mutableStateOf<Anchor?>(null)
    val firstAnchor = _firstAnchor
    private val _secondAnchor = mutableStateOf<Anchor?>(null)
    val secondAnchor = _secondAnchor
    private val _thirdAnchor = mutableStateOf<Anchor?>(null)
    val thirdAnchor = _thirdAnchor

    private val _areaFirstAnchor = mutableStateOf<Anchor?>(null)
    val areaFirstAnchor = _areaFirstAnchor

    private val _areaSecondAnchor = mutableStateOf<Anchor?>(null)
    val areaSecondAnchor = _areaSecondAnchor

    private val _measuredDistance = mutableStateOf<Float?>(null)
    val measuredDistance = _measuredDistance

    private val _measuredArea = mutableStateOf<Float?>(null)
    val measuredArea = _measuredArea

    private val _sideDistance1 = mutableStateOf<Float?>(null)
    val sideDistance1 = _sideDistance1

    private val _sideDistance2 = mutableStateOf<Float?>(null)
    val sideDistance2 = _sideDistance2

    private val _areaSideDistance1 = mutableStateOf<Float?>(null)
    val areaSideDistance1 = _areaSideDistance1

    private val _areaSideDistance2 = mutableStateOf<Float?>(null)
    val areaSideDistance2 = _areaSideDistance2

    private val _measurementHistory = mutableStateListOf<Float>()
    val measurementHistory = _measurementHistory

    private val _showHistory = mutableStateOf(false)
    val showHistory = _showHistory

    private val _showCalculateAreaButton = mutableStateOf(false)
    val showCalculateAreaButton = _showCalculateAreaButton

    private val _measurementPoints = mutableStateListOf<AnchorNode>()
    val measurementPoints = _measurementPoints

    // Modo revestimiento
    private val _isCoatingMode = mutableStateOf(false)
    val isCoatingMode = _isCoatingMode
    private val _processedPlanes = mutableStateListOf<Int>()
    val processedPlanes = _processedPlanes

    // NUEVO: Nodo único para el modo baldosa
    private var _tileNode: Node? = null
    val tileNode: Node?
        get() = _tileNode

    // NUEVO: Lista para almacenar modelos colocados en la escena
    private val _placedARModels = mutableStateListOf<PlacedARModel>()
    val placedARModels = _placedARModels

    // NUEVO: Modelo seleccionado para edición/eliminación
    private val _selectedPlacedModel = mutableStateOf<PlacedARModel?>(null)
    val selectedPlacedModel = _selectedPlacedModel

    // NUEVO: Nodo 3D del botón de eliminar
    private val _deleteButtonNode = mutableStateOf<SphereNode?>(null)
    val deleteButtonNode = _deleteButtonNode

    // NUEVO: Lista de modelos disponibles
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

    // NUEVO: Modelo seleccionado para edición/eliminación
    var selectedModel =
        mutableStateOf<ARModel?>(availableModels.firstOrNull()) // Seleccionar el primer modelo por defecto
        set(value) {
            Log.d(
                "AR_DEBUG",
                "Cambiando modelo seleccionado a: ${value.value?.name}, path: ${value.value?.modelPath}"
            )
            field = value
        }

    // Estado para el menú desplegable
    var isDropdownExpanded = mutableStateOf(false)

    // Cache para modelos de baldosa para evitar recargas
    private val tileModelCache = mutableMapOf<String, List<ModelInstance>>()
    private val coatingNodesCache = mutableListOf<AnchorNode>()

    // OPTIMIZACIÓN: Control de frecuencia de creación de baldosas
    private var lastTileCreationTime = 0L
    private val tileCreationCooldown = 500L // 500ms entre creaciones de baldosas
    private var isCreatingTiles = false

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

        // Usar el mapa de instancias por modelo
        val modelPath = kModelFile
        val modelPool = _modelInstancesMap.getOrPut(modelPath) {
            mutableStateListOf<ModelInstance>().apply {
                if (isEmpty()) {
                    Log.d("AR_DEBUG", "Cargando nuevo modelo desde archivo: $modelPath")
                    this += modelLoader.createInstancedModel(modelPath, kMaxModelInstances)
                }
            }
        }

        // Verificar si hay instancias disponibles, si no, crear más
        if (modelPool.isEmpty()) {
            Log.d("AR_DEBUG", "Pool vacío, creando más instancias para: $modelPath")
            modelPool += modelLoader.createInstancedModel(modelPath, kMaxModelInstances)
        }

        val modelNode = ModelNode(
            modelInstance = modelPool.removeAt(modelPool.size - 1),
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

        // NUEVO: Crear objeto PlacedARModel y agregar a la lista
        val placedARModel = PlacedARModel(
            anchorNode = anchorNode,
            modelNode = modelNode,
            boundingBoxNode = boundingBoxNode
        )
        _placedARModels.add(placedARModel)

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

        // Almacenar las distancias para mostrarlas en la UI (usar areaSideDistance para área)
        _areaSideDistance1.value = distanceP1P2
        _areaSideDistance2.value = distanceP2P3
        
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
        session: Session
    ): AnchorNode? {
        Log.d("AR_DEBUG", "Creando baldosa individual en el centro del plano: ${planeExtentX}m x ${planeExtentZ}m")
        
        // OPTIMIZACIÓN: Control de frecuencia para evitar sobrecarga
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTileCreationTime < tileCreationCooldown || isCreatingTiles) {
            Log.d("AR_DEBUG", "Saltando creación de baldosa - muy frecuente o ya en proceso")
            return null
        }
        
        // OPTIMIZACIÓN: Validar estado de tracking antes de proceder
        if (!isTrackingValid(anchor)) {
            Log.w("AR_DEBUG", "Anchor no válido para crear baldosa - estado: ${anchor.trackingState}")
            return null
        }
        
        isCreatingTiles = true
        lastTileCreationTime = currentTime
        
        val tileSize = 0.6f // Tamaño real de baldosa en metros
        
        try {
            Log.d("AR_DEBUG", "Creando una sola baldosa en el centro del plano")
            
            // MODIFICACIÓN: Crear solo una instancia de baldosa
            val tileInstance = try {
                modelLoader.createInstancedModel("models/baldosa.glb", 1).first()
            } catch (e: Exception) {
                Log.e("AR_DEBUG", "Error creando instancia de baldosa: ${e.message}")
                return null
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
                worldPosition = Float3(0f, 0f, 0f)
            }
            
            // Añadir la baldosa única al anchor principal
            mainAnchorNode.addChildNode(modelNode)

            // NUEVO: Asignar el nodo de baldosa a la propiedad tileNode
            _tileNode = mainAnchorNode
            
            Log.d("AR_DEBUG", "Baldosa individual creada y anclada correctamente en el centro del plano")

            return mainAnchorNode

        } catch (e: Exception) {
            Log.e("AR_DEBUG", "Error en createTiledCoatingNode: ${e.message}")
            return null
        } finally {
            isCreatingTiles = false
        }
    }

    // NUEVO: Función para resetear el nodo de baldosa
    fun resetTileNode() {
        _tileNode = null
        Log.d("AR_DEBUG", "TileNode reseteado")
    }

    // NUEVO: Función para colocar baldosa automáticamente en el primer plano horizontal encontrado
    fun tryAutoPlaceTile(
        engine: Engine,
        modelLoader: ModelLoader,
        materialLoader: MaterialLoader,
        session: Session?,
        frame: Frame?
    ): AnchorNode? {
        if (_tileNode != null || session == null || frame == null) {
            return null
        }

        try {
            // Buscar planos horizontales detectados
            val planes = session.getAllTrackables(Plane::class.java)
            val horizontalPlanes = planes.filter { plane ->
                plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING &&
                        plane.trackingState == TrackingState.TRACKING
            }

            if (horizontalPlanes.isNotEmpty()) {
                // Tomar el primer plano horizontal válido
                val plane = horizontalPlanes.first()
                val centerPose = plane.centerPose

                // Crear anchor en el centro del plano
                val anchor = session.createAnchor(centerPose)

                if (anchor != null) {
                    Log.d("AR_DEBUG", "Colocando baldosa automáticamente en plano horizontal")
                    return createTiledCoatingNode(
                        engine = engine,
                        modelLoader = modelLoader,
                        materialLoader = materialLoader,
                        anchor = anchor,
                        planeExtentX = plane.extentX,
                        planeExtentZ = plane.extentZ,
                        session = session
                    )
                }
            }
        } catch (e: Exception) {
            Log.w("AR_DEBUG", "Error en colocación automática de baldosa: ${e.message}")
        }

        return null
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

    // OPTIMIZACIÓN: Validar estado de tracking
    private fun isTrackingValid(anchor: Anchor?): Boolean {
        return anchor != null && anchor.trackingState == TrackingState.TRACKING
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

    // NUEVO: Eliminar completamente todas las baldosas de la escena
    fun removeAllTileNodes(childNodes: MutableList<Node>) {
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

    // NUEVO: Función para seleccionar un modelo AR con botón 3D
    fun selectPlacedModel(
        placedModel: PlacedARModel?,
        engine: Engine,
        materialLoader: MaterialLoader
    ) {
        // Limpiar botón anterior si existe
        _selectedPlacedModel.value?.let { previousModel ->
            removeDelete3DButton(previousModel)
        }

        _selectedPlacedModel.value = placedModel

        if (placedModel != null) {
            // Crear botón 3D para el nuevo modelo seleccionado
            createDelete3DButton(engine, materialLoader, placedModel)
            Log.d("AR_DEBUG", "Modelo seleccionado con botón 3D: ${placedModel.id}")
        } else {
            Log.d("AR_DEBUG", "Modelo deseleccionado")
        }
    }

    // NUEVO: Función para eliminar el botón de eliminación 3D
    private fun removeDelete3DButton(placedModel: PlacedARModel) {
        try {
            _deleteButtonNode.value?.let { buttonNode ->
                placedModel.anchorNode.removeChildNode(buttonNode)
                _deleteButtonNode.value = null
                Log.d("AR_DEBUG", "Botón 3D removido del modelo")
            }
        } catch (e: Exception) {
            Log.e("AR_DEBUG", "Error removiendo botón 3D: ${e.message}")
        }
    }

    // NUEVO: Función para crear un botón de eliminación 3D
    private fun createDelete3DButton(
        engine: Engine,
        materialLoader: MaterialLoader,
        placedModel: PlacedARModel
    ) {
        try {
            val modelNode = placedModel.modelNode
            val anchorNode = placedModel.anchorNode

            // Calcular posición 3D del botón basada en las dimensiones del modelo
            val modelExtents = modelNode.extents
            val modelScale = modelNode.worldScale
            val modelHeight = modelExtents.y * modelScale.y

            // Crear un botón esférico 3D rojo
            val deleteButton = SphereNode(
                engine = engine,
                radius = 0.03f, // 3cm de radio
                materialInstance = materialLoader.createColorInstance(Color.Red.copy(alpha = 0.8f))
            ).apply {
                // Posicionar el botón directamente encima del modelo en 3D
                position = Float3(0f, modelHeight + 0.05f, 0f)
                isVisible = true
            }

            // Agregar el botón como hijo directo del anchor del modelo
            anchorNode.addChildNode(deleteButton)
            _deleteButtonNode.value = deleteButton

            Log.d(
                "AR_DEBUG",
                "Botón 3D creado y agregado al modelo en posición Y: ${modelHeight + 0.05f}"
            )

        } catch (e: Exception) {
            Log.e("AR_DEBUG", "Error creando botón 3D: ${e.message}")
        }
    }

    // NUEVO: Función para encontrar un modelo por su nodo
    fun findPlacedModelByNode(node: Node): PlacedARModel? {
        return _placedARModels.find {
            it.anchorNode == node || it.modelNode == node || it.boundingBoxNode == node
        }
    }

    // NUEVO: Función para verificar si se tocó el botón de eliminar
    fun isDeleteButtonNode(node: Node): Boolean {
        return _deleteButtonNode.value == node
    }

    // NUEVO: Función para manejar el toque del botón de eliminar
    fun handleDeleteButtonTouch(childNodes: MutableList<Node>) {
        _selectedPlacedModel.value?.let { selectedModel ->
            removePlacedModel(selectedModel, childNodes)
        }
    }

    // NUEVO: Función para eliminar un modelo específico
    fun removePlacedModel(
        placedModel: PlacedARModel,
        childNodes: MutableList<Node>
    ) {
        Log.d("AR_DEBUG", "Eliminando modelo AR: ${placedModel.id}")

        try {
            val anchorNode = placedModel.anchorNode

            val modelNode = placedModel.modelNode

            val boundingBoxNode = placedModel.boundingBoxNode

            val anchor = anchorNode.anchor

            Log.d("AR_DEBUG", "Iniciando limpieza completa del modelo")

            // 1. Limpiar selección si era el modelo seleccionado
            if (_selectedPlacedModel.value == placedModel) {
                _selectedPlacedModel.value = null
                Log.d("AR_DEBUG", "Modelo deseleccionado")
            }

            // 2. Remover todos los nodos hijos del modelo
            modelNode.removeChildNode(boundingBoxNode)

            anchorNode.removeChildNode(modelNode)

            Log.d("AR_DEBUG", "Nodos hijos eliminados")

            // 3. Remover de la escena
            childNodes.remove(anchorNode)

            Log.d("AR_DEBUG", "AnchorNode removido de la escena")

            // 4. Desconectar y limpiar el anchor de AR Core
            if (anchor != null) {
                try {
                    anchor.detach()
                    Log.d("AR_DEBUG", "Anchor desconectado de AR Core")
                } catch (e: Exception) {
                    Log.w("AR_DEBUG", "Error desconectando anchor: ${e.message}")
                }
            }

            // 5. Devolver la instancia del modelo al pool correcto para reutilización
            val modelInstance = modelNode.modelInstance

            val modelPath = kModelFile // Obtener el path del modelo actual

            // Buscar el pool correcto para este tipo de modelo
            _modelInstancesMap[modelPath]?.let { pool ->
                if (pool.size < kMaxModelInstances) {
                    pool.add(modelInstance)
                    Log.d("AR_DEBUG", "Instancia de modelo devuelta al pool: $modelPath")
                }
            }

            // 6. Remover de la lista de modelos colocados
            _placedARModels.remove(placedModel)

            Log.d(
                "AR_DEBUG",
                "Modelo AR eliminado completamente. Modelos restantes: ${_placedARModels.size}"
            )
        } catch (e: Exception) {
            Log.e("AR_DEBUG", "Error eliminando modelo AR: ${e.message}", e)
        }
    }

    // NUEVO: Función auxiliar para multiplicar matriz 4x4 por vector 4D
    private fun multiplyMatrixVector(matrix: FloatArray, vector: FloatArray, result: FloatArray) {
        result[0] =
            matrix[0] * vector[0] + matrix[4] * vector[1] + matrix[8] * vector[2] + matrix[12] * vector[3]

        result[1] =
            matrix[1] * vector[0] + matrix[5] * vector[1] + matrix[9] * vector[2] + matrix[13] * vector[3]

        result[2] =
            matrix[2] * vector[0] + matrix[6] * vector[1] + matrix[10] * vector[2] + matrix[14] * vector[3]

        result[3] =
            matrix[3] * vector[0] + matrix[7] * vector[1] + matrix[11] * vector[2] + matrix[15] * vector[3]
    }

    // NUEVO: Función para proyectar coordenadas del mundo a coordenadas de pantalla
    private fun projectWorldToScreen(
        worldX: Float,
        worldY: Float,
        worldZ: Float,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        screenWidth: Float,
        screenHeight: Float
    ): Pair<Float, Float> {
        // Vector de posición mundial en coordenadas homogéneas
        val worldPos = floatArrayOf(worldX, worldY, worldZ, 1.0f)

        val viewPos = FloatArray(4)

        val clipPos = FloatArray(4)

        // Multiplicar por matriz de vista primero
        multiplyMatrixVector(viewMatrix, worldPos, viewPos)

        // Luego multiplicar por matriz de proyección
        multiplyMatrixVector(projectionMatrix, viewPos, clipPos)

        // Dividir por w para obtener coordenadas de dispositivo normalizadas (NDC)
        if (clipPos[3] != 0.0f && clipPos[3] > 0.0f) { // Verificar que esté frente a la cámara
            val ndcX = clipPos[0] / clipPos[3]

            val ndcY = clipPos[1] / clipPos[3]

            // Convertir NDC (-1 a 1) a coordenadas de pantalla (0 a width/height)
            val screenX = (ndcX + 1.0f) * 0.5f * screenWidth

            val screenY =
                (1.0f - ndcY) * 0.5f * screenHeight  // Invertir Y porque en pantalla Y=0 está arriba

            return Pair(screenX, screenY)
        }

        // Si el punto está detrás de la cámara o la proyección falla, retornar fuera de pantalla
        return Pair(-100f, -100f)
    }

    // NUEVO: Función para obtener la distancia del modelo seleccionado a la cámara (para escalar el botón)
    fun getSelectedModelDistanceToCamera(): Float? {
        return try {
            val placedModel = _selectedPlacedModel.value ?: return null

            val anchor = placedModel.anchorNode.anchor ?: return null

            val frame = _frame.value ?: return null

            val anchorPose = anchor.pose

            val cameraPos = frame.camera.pose

            kotlin.math.sqrt(
                (anchorPose.tx() - cameraPos.tx()).let { it * it } +
                        (anchorPose.ty() - cameraPos.ty()).let { it * it } +
                        (anchorPose.tz() - cameraPos.tz()).let { it * it }
            )
        } catch (e: Exception) {
            Log.w("AR_DEBUG", "Error calculando distancia a cámara: ${e.message}")

            null
        }
    }

    // NUEVO: Función para actualizar dimensiones de pantalla
    fun updateScreenDimensions(width: Float, height: Float) {
        screenWidth = width

        screenHeight = height

        Log.d("AR_DEBUG", "Dimensiones de pantalla actualizadas: ${width}x${height}")
    }

    // NUEVO: Función para actualizar la posición del botón durante el frame
    fun updateSelectedModelPosition() {
        _selectedPlacedModel.value?.let { placedModel ->
            // Función updateSelectedModelScreenPosition eliminada
        }
    }
}
