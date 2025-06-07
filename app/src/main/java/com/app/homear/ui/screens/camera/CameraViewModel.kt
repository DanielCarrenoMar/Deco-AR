package com.app.homear.ui.screens.camera

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.app.homear.domain.usecase.GetModelFilesFromDirUseCase
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Frame
import com.google.ar.core.TrackingFailureReason
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.SphereNode
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(

): ViewModel(){
    // Testing
    private val kModelFile = "models/apple.glb"
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

    // Medicion de distancias

    private val _isMeasuring = mutableStateOf(false)
    val isMeasuring = _isMeasuring

    private val _firstAnchor = mutableStateOf<Anchor?>(null)
    val firstAnchor = _firstAnchor
    private val _secondAnchor = mutableStateOf<Anchor?>(null)
    val secondAnchor = _secondAnchor

    private val _measuredDistance = mutableStateOf<Float?>(null)
    val measuredDistance = _measuredDistance

    private val _measurementHistory = mutableStateListOf<Float>()
    val measurementHistory = _measurementHistory
    private val _showHistory = mutableStateOf(false)
    val showHistory = _showHistory
    private val _measurementPoints = mutableStateListOf<AnchorNode>()
    val measurementPoints = _measurementPoints

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
}
