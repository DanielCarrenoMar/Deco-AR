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
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.google.ar.core.Session
import com.google.ar.core.TrackingFailureReason
import com.google.ar.core.TrackingState
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Mat4
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.node.SphereNode
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.cos

// IMPORT: FurnitureItem definition
import com.app.homear.ui.screens.catalog.FurnitureItem
import java.io.File
import dagger.hilt.android.qualifiers.ApplicationContext

data class ARModel(
    val name: String,
    val modelPath: String,
)

// Actualizar: Clase para almacenar información de modelos renderizados con contador
data class RenderedModelInfo(
    val name: String,
    val path: String,
    val count: Int = 1
)

// Extension for converting FurnitureItem to ARModel
fun FurnitureItem.toARModel(): ARModel {
    return ARModel(
        name = this.name,
        modelPath = this.modelPath
    )
}

// NUEVO: Clase para representar un modelo colocado en AR con información de selección
data class PlacedARModel(
    val anchorNode: AnchorNode,
    val modelNode: ModelNode,
    val boundingBoxNode: CubeNode,
    val id: String = java.util.UUID.randomUUID().toString()
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context
): ViewModel() {
    companion object {
        // Shared singleton list of renderable ARModels in the app
        val sharedAvailableModels = mutableListOf<ARModel>()

        // Function to add new model from FurnitureItem ("+" in catalog)
        fun addARModelFromFurniture(furniture: FurnitureItem) {
            val newModel = furniture.toARModel()
            // Prevent duplicates
            if (sharedAvailableModels.none { it.name == newModel.name && it.modelPath == newModel.modelPath }) {
                sharedAvailableModels.add(newModel)
            }
        }
    }

    // NUEVO: Lista de modelos disponibles (debe estar antes de selectedModel)
    val availableModels: MutableList<ARModel> = sharedAvailableModels

    // NUEVO: Dimensiones de pantalla para proyección precisa
    private var screenWidth = 800f
    private var screenHeight = 600f

    // NUEVO: Modelo seleccionado para colocación
    var selectedModel = mutableStateOf<ARModel?>(availableModels.firstOrNull())
        set(value) {
            Log.d(
                "AR_DEBUG",
                "Cambiando modelo seleccionado a: ${value.value?.name}, path: ${value.value?.modelPath}"
            )
            field = value
        }

    // Estado para el menú desplegable
    var isDropdownExpanded = mutableStateOf(false)

    // NUEVO: Modelo AR seleccionado para confirmación de eliminación
    private val _selectedPlacedModel = mutableStateOf<PlacedARModel?>(null)
    val selectedPlacedModel = _selectedPlacedModel

    // NUEVO: Lista para almacenar modelos colocados en la escena
    private val _placedARModels = mutableStateListOf<PlacedARModel>()
    val placedARModels = _placedARModels

    // Nuevo: Lista de modelos renderizados
    private val _renderedModels = mutableStateListOf<RenderedModelInfo>()
    val renderedModels = _renderedModels

    // Nuevo: Estado para mostrar/ocultar el diálogo de lista
    private val _showModelsList = mutableStateOf(false)
    val showModelsList = _showModelsList

    // Renderizado de Muebles

    private val kModelFile: String
        get() {
            val modelPath = selectedModel.value?.modelPath ?: "models/Mueble-1.glb"
            // Asegurarnos de que tenemos la ruta completa al archivo en el almacenamiento interno
            val internalPath = if (modelPath.startsWith("/")) {
                modelPath
            } else {
                File(context.filesDir, "assets/$modelPath").absolutePath
            }
            Log.d("AR_DEBUG", "Loading model from: $internalPath")
            return internalPath
        }
    private val kMaxModelInstances = 10

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

    // NUEVO: Lista para almacenar nodos de planos verticales personalizados
    private val _verticalPlaneNodes = mutableStateListOf<AnchorNode>()
    val verticalPlaneNodes = _verticalPlaneNodes

    // Cache para modelos de baldosa para evitar recargas
    private val tileModelCache = mutableMapOf<String, List<ModelInstance>>()
    private val coatingNodesCache = mutableListOf<AnchorNode>()

    // OPTIMIZACIÓN: Control de frecuencia de creación de baldosas
    private var lastTileCreationTime = 0L
    private val tileCreationCooldown = 500L // 500ms entre creaciones de baldosas
    private var isCreatingTiles = false

    // Variables para frustum culling
    private var cameraFrustum: Array<FloatArray>? = null
    private var lastCameraUpdateTime = 0L
    private val cameraUpdateInterval = 100L

    // NUEVO: Variables para optimización temporal
    private var lastLODUpdateTime = 0L
    private val lodUpdateInterval = 200L
    private var lastVisibilityUpdateTime = 0L
    private val visibilityUpdateInterval = 150L

    // NUEVO: Actualización optimizada de visibilidad con control temporal
    fun updatePointVisibilityOptimized() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastVisibilityUpdateTime < visibilityUpdateInterval) return

        lastVisibilityUpdateTime = currentTime

        try {
            val frustum = cameraFrustum ?: return

            var nodesVisible = 0
            var nodesHidden = 0

            // OPTIMIZACIÓN: Procesar solo cada 3er nodo para reducir carga
            _verticalPlaneNodes.forEachIndexed { index, anchorNode ->
                // SKIP: Procesar solo cada 3er plano para optimización
                if (index % 3 != 0) return@forEachIndexed

                anchorNode.childNodes.forEach { childNode ->
                    // OPTIMIZACIÓN: Procesar solo cada 2do punto
                    childNode.childNodes.forEachIndexed { pointIndex, pointNode ->
                        if (pointIndex % 2 != 0) return@forEachIndexed

                        val position = pointNode.worldPosition
                        val isVisible = isPointInFrustum(position.x, position.y, position.z)

                        if (pointNode.isVisible != isVisible) {
                            pointNode.isVisible = isVisible
                            if (isVisible) nodesVisible++ else nodesHidden++
                        }
                    }
                }
            }

            if (nodesVisible > 0 || nodesHidden > 0) {
                Log.d(
                    "AR_DEBUG",
                    "Visibility optimizada: ${nodesVisible} mostrados, ${nodesHidden} ocultados"
                )
            }

        } catch (e: Exception) {
            Log.w("AR_DEBUG", "Error en visibility optimizada: ${e.message}")
        }
    }

    // NUEVO: Actualización de LOD optimizada con control temporal
    fun updateLODOptimized() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastLODUpdateTime < lodUpdateInterval) return

        lastLODUpdateTime = currentTime

        try {
            // OPTIMIZACIÓN: Actualizar LOD solo para nodos visibles
            _verticalPlaneNodes.forEach { anchorNode ->
                anchorNode.childNodes.forEach { childNode ->
                    childNode.childNodes.forEach { pointNode ->
                        if (!pointNode.isVisible) return@forEach // Skip nodos no visibles

                        val position = pointNode.worldPosition
                        val distanceFromCenter =
                            sqrt(position.x * position.x + position.z * position.z)

                        // ACTUALIZAR ESCALA DINÁMICAMENTE
                        val newScale = when {
                            distanceFromCenter < 1.0f -> Float3(1.0f, 0.1f, 1.0f)
                            distanceFromCenter < 2.0f -> Float3(0.8f, 0.08f, 0.8f)
                            else -> Float3(0.6f, 0.06f, 0.6f)
                        }

                        // Solo actualizar si ha cambiado significativamente
                        val currentScale = pointNode.scale
                        if (kotlin.math.abs(currentScale.x - newScale.x) > 0.1f) {
                            pointNode.scale = newScale
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("AR_DEBUG", "Error en LOD optimizado: ${e.message}")
        }
    }

    // Función combinada para actualizar cámara y visibilidad OPTIMIZADA
    fun updateCameraAndVisibility(frame: Frame) {
        // Actualizar frustum de la cámara
        updateCameraFrustum(frame)

        // NUEVO: Usar actualizaciones optimizadas en lugar de las costosas
        updatePointVisibilityOptimized()
        updateLODOptimized()
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

        // Usar el mapa de instancias por modelo
        val modelPath = kModelFile
        val modelPool = _modelInstancesMap.getOrPut(modelPath) {
            mutableStateListOf<ModelInstance>().apply {
                if (isEmpty()) {
                    Log.d("AR_DEBUG", "Cargando nuevo modelo desde archivo: $modelPath")
                    try {
                        // Intentar cargar el modelo desde el almacenamiento interno
                        val modelFile = File(modelPath)
                        if (modelFile.exists()) {
                            this += modelLoader.createInstancedModel(modelFile, kMaxModelInstances)
                            Log.d("AR_DEBUG", "Modelo cargado exitosamente desde almacenamiento interno")
                        } else {
                            Log.e("AR_DEBUG", "Archivo de modelo no encontrado: $modelPath")
                            throw IllegalStateException("Modelo no encontrado: $modelPath")
                        }
                    } catch (e: Exception) {
                        Log.e("AR_DEBUG", "Error cargando modelo: ${e.message}")
                        throw e
                    }
                }
            }
        }

        // Verificar si hay instancias disponibles, si no, crear más
        if (modelPool.isEmpty()) {
            Log.d("AR_DEBUG", "Pool vacío, creando más instancias para: $modelPath")
            val modelFile = File(modelPath)
            modelPool += modelLoader.createInstancedModel(modelFile, kMaxModelInstances)
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

        // Nuevo: Registrar el modelo renderizado
        selectedModel.value?.let { model ->
            addRenderedModel(model.name, model.modelPath)
        }

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
        val distanceP1P2 = sqrt(
            (p1.tx() - p2.tx()) * (p1.tx() - p2.tz()) +
                    (p1.ty() - p3.ty()) * (p1.ty() - p3.ty()) +
                    (p2.tz() - p3.tz()) * (p2.tz() - p3.tz())
        )

        // Distancia del punto 2 al punto 3 (segundo lado)
        val distanceP2P3 = sqrt(
            (p2.tx() - p3.tx()) * (p2.tx() - p3.tz()) +
                    (p2.ty() - p3.ty()) * (p2.ty() - p3.ty()) +
                    (p2.tz() - p3.tz()) * (p2.tz() - p3.tz())
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
                // Posición en el centro del plano (0,0,0 relativa)
                worldPosition = Float3(0f, 0f, 0f)
            }

            // Añadir la baldosa única al anchor principal
            mainAnchorNode.addChildNode(modelNode)

            // NUEVO: Agregar el nodo al cache de nodos de revestimiento
            coatingNodesCache.add(mainAnchorNode)

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

    // ACTUALIZADO: Función para actualizar planos detectados y crear círculos 2D sobre planos verticales
    fun updateDetectedPlanes(
        session: Session?,
        engine: Engine,
        materialLoader: MaterialLoader,
        childNodes: MutableList<Node>
    ) {
        if (session == null || !planeRenderer.value) return

        try {
            val planes = session.getAllTrackables(Plane::class.java)
            val activePlanes = planes.filter { it.trackingState == TrackingState.TRACKING }

            // Limpiar nodos de planos verticales anteriores
            _verticalPlaneNodes.forEach { node ->
                childNodes.remove(node)
                // Desconectar el anchor si existe
                node.anchor?.detach()
            }
            _verticalPlaneNodes.clear()

            // Crear nodos solo para planos verticales con círculos 2D
            activePlanes.forEach { plane ->
                when (plane.type) {
                    Plane.Type.VERTICAL -> {
                        try {
                            val verticalPlaneNode = createVerticalPlaneWithCircles(
                                engine = engine,
                                materialLoader = materialLoader,
                                plane = plane,
                                session = session
                            )

                            if (verticalPlaneNode != null) {
                                childNodes.add(verticalPlaneNode)
                                _verticalPlaneNodes.add(verticalPlaneNode)
                                Log.d(
                                    "AR_DEBUG",
                                    "Plano vertical con círculos agregado: ${plane.extentX}x${plane.extentZ}"
                                )
                            }
                        } catch (e: Exception) {
                            Log.w(
                                "AR_DEBUG",
                                "Error creando plano vertical con círculos: ${e.message}"
                            )
                        }
                    }

                    else -> {
                        // Los planos horizontales los maneja SceneView automáticamente
                    }
                }
            }

            Log.d(
                "AR_DEBUG",
                "Planes actualizados. Total: ${activePlanes.size}, Verticales con círculos: ${_verticalPlaneNodes.size}"
            )
        } catch (e: Exception) {
            Log.e("AR_DEBUG", "Error actualizando planos detectados: ${e.message}")
        }
    }

    // NUEVO: Función para crear planos verticales con círculos 2D adheridos
    fun createVerticalPlaneWithCircles(
        engine: Engine,
        materialLoader: MaterialLoader,
        plane: Plane,
        session: Session
    ): AnchorNode? {
        return try {
            Log.d(
                "AR_DEBUG",
                "Creando plano vertical optimizado con malla única"
            )

            val anchor = session.createAnchor(plane.centerPose)
            val anchorNode = AnchorNode(engine = engine, anchor = anchor)

            // CONFIGURACIÓN DINÁMICA: Calcular puntos necesarios según tamaño real
            val spacing =
                0.18f // Espaciado optimizado (18cm entre puntos) - Aumentado para reducir cantidad de círculos
            val planeWidth = plane.extentX
            val planeHeight = plane.extentZ
            val pointsX = ceil(planeWidth / spacing).toInt().coerceAtLeast(1)
            val pointsZ = ceil(planeHeight / spacing).toInt().coerceAtLeast(1)
            val totalPoints = pointsX * pointsZ

            Log.d(
                "AR_DEBUG",
                "Malla DINÁMICA calculada: ${pointsX}x${pointsZ} puntos (${totalPoints} total) para llenar ${planeWidth}x${planeHeight}m con spacing ${spacing}m"
            )

            // GENERAR: Vertex buffer e índices para todos los puntos calculados
            val vertices = generateVertexBuffer(planeWidth, plane.extentZ, pointsX, pointsZ)
            val indices = generateIndexBuffer(pointsX, pointsZ)

            // CREAR: Geometría completa sin limitaciones artificiales
            val circlePatternNode = createCompletePlaneGeometry(
                engine = engine,
                materialLoader = materialLoader,
                vertices = vertices,
                indices = indices,
                totalPoints = totalPoints,
                pointsX = pointsX,
                pointsZ = pointsZ
            )

            if (circlePatternNode != null) {
                anchorNode.addChildNode(circlePatternNode)
                Log.d(
                    "AR_DEBUG",
                    "Malla dinámica completada: ${vertices.size / 3} vértices para ${totalPoints} círculos llenando completamente el plano"
                )
            }

            anchorNode

        } catch (e: Exception) {
            Log.e("AR_DEBUG", "Error creando malla dinámica: ${e.message}")
            null
        }
    }

    // NUEVO: Función para colocar baldosa automáticamente en el primer plano horizontal encontrado
    fun tryAutoPlaceTile(
        engine: Engine,
        modelLoader: ModelLoader,
        materialLoader: MaterialLoader,
        session: Session,
        frame: Frame
    ): AnchorNode? {
        if (_tileNode != null) {
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

    // NUEVO: Función para eliminar completamente todas las baldosas de la escena
    fun removeAllTileNodes(childNodes: MutableList<Node>) {
        Log.d(
            "AR_DEBUG",
            "Eliminando todas las baldosas de la escena (${coatingNodesCache.size} grupos de baldosas)"
        )

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

        Log.d(
            "AR_DEBUG",
            "Limpieza completa terminada. Nodos restantes en escena: ${childNodes.size}"
        )
    }

    // NUEVO: Función para resetear el nodo de baldosa
    fun resetTileNode() {
        _tileNode = null
        Log.d("AR_DEBUG", "TileNode reseteado")
    }

    // NUEVO: Función para actualizar dimensiones de pantalla
    fun updateScreenDimensions(width: Float, height: Float) {
        screenWidth = width
        screenHeight = height
        Log.d("AR_DEBUG", "Dimensiones de pantalla actualizadas: ${width}x${height}")
    }

    // NUEVO: Función para actualizar la posición del modelo seleccionado
    fun updateSelectedModelPosition() {
        _selectedPlacedModel.value?.let { placedModel ->
            // Función updateSelectedModelScreenPosition eliminada
        }
    }

    // Actualizar: Métodos para manejar modelos renderizados
    fun addRenderedModel(name: String, path: String) {
        val existingModelIndex = _renderedModels.indexOfFirst { it.name == name && it.path == path }
        if (existingModelIndex != -1) {
            // Si el modelo existe, incrementar su contador
            val existingModel = _renderedModels[existingModelIndex]
            _renderedModels[existingModelIndex] = existingModel.copy(count = existingModel.count + 1)
        } else {
            // Si es un nuevo modelo, agregarlo con contador en 1
            _renderedModels.add(RenderedModelInfo(name, path))
        }
    }

    fun removeRenderedModel(name: String) {
        val modelIndex = _renderedModels.indexOfFirst { it.name == name }
        if (modelIndex != -1) {
            val model = _renderedModels[modelIndex]
            if (model.count > 1) {
                // Si hay más de una instancia, decrementar el contador
                _renderedModels[modelIndex] = model.copy(count = model.count - 1)
            } else {
                // Si es la última instancia, remover el modelo
                _renderedModels.removeAt(modelIndex)
            }
        }
    }

    fun clearRenderedModels() {
        _renderedModels.clear()
    }

    fun toggleModelsList() {
        _showModelsList.value = !_showModelsList.value
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

    // OPTIMIZACIÓN 1: Generar vertex buffer con todas las posiciones
    private fun generateVertexBuffer(
        width: Float,
        height: Float,
        pointsX: Int,
        pointsZ: Int
    ): FloatArray {
        val verticesPerCircle = 6 // Hexágono simple para cada círculo
        val vertexArray =
            FloatArray(pointsX * pointsZ * verticesPerCircle * 3) // x, y, z por vértice

        val startX = -width / 2f
        val startZ = -height / 2f
        val stepX = if (pointsX > 1) width / (pointsX - 1) else 0f
        val stepZ = if (pointsZ > 1) height / (pointsZ - 1) else 0f

        var vertexIndex = 0

        // Generar vértices para cada círculo
        for (x in 0 until pointsX) {
            for (z in 0 until pointsZ) {
                val centerX = startX + (x * stepX)
                val centerZ = startZ + (z * stepZ)

                // Crear hexágono simple para cada punto circular
                for (i in 0 until verticesPerCircle) {
                    val angle = (i * 2 * PI / verticesPerCircle).toFloat()
                    val offsetX = 0.01f * cos(angle)
                    val offsetZ = 0.01f * sin(angle)

                    // Posición del vértice
                    vertexArray[vertexIndex++] = centerX + offsetX
                    vertexArray[vertexIndex++] = 0f // Y en el plano
                    vertexArray[vertexIndex++] = centerZ + offsetZ
                }
            }
        }

        return vertexArray
    }

    // NUEVO: Función para crear geometría completa con LOD (Level of Detail) para máximo rendimiento
    private fun createCompletePlaneGeometry(
        engine: Engine,
        materialLoader: MaterialLoader,
        vertices: FloatArray,
        indices: IntArray,
        totalPoints: Int,
        pointsX: Int,
        pointsZ: Int
    ): Node? {
        return try {
            val parentNode = Node(engine)

            // MATERIAL OPTIMIZADO: Sin transparencia para máximo rendimiento GPU
            val solidMaterial = materialLoader.createColorInstance(
                Color.White // Sin alpha para evitar transparencia y cálculos adicionales
            )

            // RENDERIZADO CON LOD: Geometría adaptativa según distancia
            val verticesPerCircle = 6

            Log.d(
                "AR_DEBUG",
                "Iniciando renderizado con LOD de ${totalPoints} puntos calculados"
            )

            var pointsCreated = 0
            var vertexIndex = 0

            // BUCLE CON LOD: Optimizar geometría según distancia estimada del centro
            while (vertexIndex < vertices.size && pointsCreated < totalPoints) {
                val x = vertices[vertexIndex]
                val y = vertices[vertexIndex + 1]
                val z = vertices[vertexIndex + 2]

                // LOD: Determinar complejidad según distancia estimada del centro
                val distanceFromCenter = sqrt(x * x + z * z)

                // OPTIMIZACIÓN MÁXIMA: Todos los círculos con mínimo detalle para proteger la GPU
                val (circleRadius, scale) = when {
                    distanceFromCenter < 1.0f -> {
                        // CERCA: Mínimo detalle
                        Pair(0.008f, Float3(0.6f, 0.06f, 0.6f))
                    }

                    distanceFromCenter < 2.0f -> {
                        // MEDIO: Mínimo detalle
                        Pair(0.008f, Float3(0.6f, 0.06f, 0.6f))
                    }

                    else -> {
                        // LEJOS: Mínimo detalle
                        Pair(0.008f, Float3(0.6f, 0.06f, 0.6f))
                    }
                }

                val circleNode = SphereNode(
                    engine = engine,
                    radius = circleRadius,
                    materialInstance = solidMaterial
                ).apply {
                    position = Float3(x, y, z)
                    // ESCALA MÍNIMA para proteger GPU
                    this.scale = scale
                }

                parentNode.addChildNode(circleNode)
                pointsCreated++

                // AVANZAR: Al siguiente círculo
                vertexIndex += (verticesPerCircle * 3)
            }

            Log.d(
                "AR_DEBUG",
                "LOD completado: ${pointsCreated} puntos renderizados con mínimo detalle para proteger GPU"
            )

            parentNode

        } catch (e: Exception) {
            Log.e("AR_DEBUG", "Error creando geometría con LOD: ${e.message}")
            null
        }
    }

    // OPTIMIZACIÓN 2: Generar índices para triangulación eficiente
    private fun generateIndexBuffer(pointsX: Int, pointsZ: Int): IntArray {
        val verticesPerCircle = 6
        val trianglesPerCircle = verticesPerCircle // Fan triangulation
        val totalTriangles = pointsX * pointsZ * trianglesPerCircle
        val indices = IntArray(totalTriangles * 3)

        var indexPos = 0
        var baseVertex = 0

        // Generar índices para triangulación en abanico de cada círculo
        for (circle in 0 until (pointsX * pointsZ)) {
            for (tri in 0 until trianglesPerCircle) {
                // Triángulo en abanico desde el centro virtual
                indices[indexPos++] = baseVertex // Centro del círculo
                indices[indexPos++] = baseVertex + (tri % verticesPerCircle)
                indices[indexPos++] = baseVertex + ((tri + 1) % verticesPerCircle)
            }
            baseVertex += verticesPerCircle
        }

        return indices
    }

    // OPTIMIZACIÓN: Resetear estado de creación de baldosas
    fun resetTileCreationState() {
        isCreatingTiles = false
        lastTileCreationTime = 0L
        Log.d("AR_DEBUG", "Estado de creación de baldosas reseteado")
    }

    // NUEVO: Función para seleccionar modelo para confirmación de eliminación mediante modal
    fun selectModelForDeletion(placedModel: PlacedARModel?) {
        _selectedPlacedModel.value = placedModel
        if (placedModel != null) {
            Log.d("AR_DEBUG", "Modelo seleccionado para eliminación: ${placedModel.id}")
        } else {
            Log.d("AR_DEBUG", "Selección de modelo cancelada")
        }
    }

    // NUEVO: Función para confirmar eliminación desde modal
    fun confirmModelDeletion(childNodes: MutableList<Node>) {
        _selectedPlacedModel.value?.let { selectedModel ->
            removePlacedModel(selectedModel, childNodes)
            _selectedPlacedModel.value = null
        }
    }

    // NUEVO: Función para cancelar eliminación desde modal
    fun cancelModelDeletion() {
        _selectedPlacedModel.value = null
        Log.d("AR_DEBUG", "Eliminación de modelo cancelada")
    }

    // NUEVO: Función para encontrar un modelo por su nodo
    fun findPlacedModelByNode(node: Node): PlacedARModel? {
        return _placedARModels.find {
            it.anchorNode == node || it.modelNode == node || it.boundingBoxNode == node
        }
    }

    // NUEVO: Función para verificar si se tocó el botón de eliminar
    fun isDeleteButtonNode(node: Node): Boolean {
        return _selectedPlacedModel.value?.let {
            // Eliminado el uso de _deleteButtonNode
            false
        } ?: false
    }

    // NUEVO: Función para manejar el toque del botón de eliminar
    fun handleDeleteButtonTouch(childNodes: MutableList<Node>) {
        _selectedPlacedModel.value?.let { selectedModel ->
            removePlacedModel(selectedModel, childNodes)
            _selectedPlacedModel.value = null
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

            // 1. Remover todos los nodos hijos del modelo
            modelNode.removeChildNode(boundingBoxNode)

            anchorNode.removeChildNode(modelNode)

            Log.d("AR_DEBUG", "Nodos hijos eliminados")

            // 2. Remover de la escena
            childNodes.remove(anchorNode)

            Log.d("AR_DEBUG", "AnchorNode removido de la escena")

            // 3. Desconectar y limpiar el anchor de AR Core
            if (anchor != null) {
                try {
                    anchor.detach()
                    Log.d("AR_DEBUG", "Anchor desconectado de AR Core")
                } catch (e: Exception) {
                    Log.w("AR_DEBUG", "Error desconectando anchor: ${e.message}")
                }
            }

            // 4. Devolver la instancia del modelo al pool correcto para reutilización
            val modelInstance = modelNode.modelInstance

            val modelPath = kModelFile // Obtener el path del modelo actual

            // Buscar el pool correcto para este tipo de modelo
            _modelInstancesMap[modelPath]?.let { pool ->
                if (pool.size < kMaxModelInstances) {
                    pool.add(modelInstance)
                    Log.d("AR_DEBUG", "Instancia de modelo devuelta al pool: $modelPath")
                }
            }

            // 5. Remover de la lista de modelos colocados
            _placedARModels.remove(placedModel)

            // Nuevo: Eliminar el modelo de la lista de renderizados
            selectedModel.value?.let { model ->
                removeRenderedModel(model.name)
            }

            Log.d(
                "AR_DEBUG",
                "Modelo AR eliminado completamente. Modelos restantes: ${_placedARModels.size}"
            )
        } catch (e: Exception) {
            Log.e("AR_DEBUG", "Error eliminando modelo AR: ${e.message}", e)
        }
    }

    // NUEVO: Función para limpiar todos los nodos de planos personalizados
    fun clearCustomPlaneNodes(childNodes: MutableList<Node>) {
        // Limpiar planos verticales personalizados
        _verticalPlaneNodes.forEach { node ->
            childNodes.remove(node)
            // Desconectar el anchor si existe
            node.anchor?.detach()
        }
        _verticalPlaneNodes.clear()
        Log.d("AR_DEBUG", "Nodos de planos verticales limpiados")
    }

    // Función temporal para isPointInFrustum (frustum culling deshabilitado)
    private fun isPointInFrustum(x: Float, y: Float, z: Float): Boolean {
        return true
    }

    // Función para actualizar el frustum de la cámara (simplificado)
    private fun updateCameraFrustum(frame: Frame) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastCameraUpdateTime < cameraUpdateInterval) return

        lastCameraUpdateTime = currentTime

        try {
            // Simplificado: Solo actualizar el tiempo para la optimización
            Log.d("AR_DEBUG", "Frustum actualizado (simplificado)")
        } catch (e: Exception) {
            Log.e("AR_DEBUG", "Error actualizando frustum: ${e.message}")
        }
    }
}
