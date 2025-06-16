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
import com.google.ar.core.TrackingFailureReason
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.romainguy.kotlin.math.Float3
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
        Log.d("AR_DEBUG", "Creando baldosas distribuidas: ${planeExtentX}m x ${planeExtentZ}m")
        
        val tileNodes = mutableListOf<AnchorNode>()
        val tileSize = 0.5f
        val tilesX = kotlin.math.ceil(planeExtentX / tileSize).toInt()
        val tilesZ = kotlin.math.ceil(planeExtentZ / tileSize).toInt()
        
        Log.d("AR_DEBUG", "Distribuyendo ${tilesX}x${tilesZ} baldosas")
        
        val totalTiles = tilesX * tilesZ
        val tileInstances = mutableListOf<ModelInstance>()
        repeat(totalTiles) {
            tileInstances.add(modelLoader.createInstancedModel("models/baldosa.glb", 1).first())
        }
        
        val startX = -planeExtentX / 2f + tileSize / 2f
        val startZ = -planeExtentZ / 2f + tileSize / 2f
        
        var instanceIndex = 0
        for (x in 0 until tilesX) {
            for (z in 0 until tilesZ) {
                if (instanceIndex < tileInstances.size) {
                    val tileX = startX + (x * tileSize)
                    val tileZ = startZ + (z * tileSize)
                    
                    val tilePose = anchor.pose.compose(
                        com.google.ar.core.Pose.makeTranslation(tileX, 0f, tileZ)
                    )
                    
                    val tileAnchor = session.createAnchor(tilePose)
                    val anchorNode = AnchorNode(engine = engine, anchor = tileAnchor)
                    val modelNode = ModelNode(
                        modelInstance = tileInstances[instanceIndex],
                        scaleToUnits = tileSize
                    ).apply {
                        isEditable = false
                    }
                    anchorNode.addChildNode(modelNode)
                    tileNodes.add(anchorNode)
                    
                    Log.d("AR_DEBUG", "Baldosa creada en posición: ($tileX, 0, $tileZ)")
                    instanceIndex++
                }
            }
        }
        
        Log.d("AR_DEBUG", "Revestimiento con ${tileNodes.size} baldosas creado exitosamente")
        return tileNodes
    }


}
