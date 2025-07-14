package com.app.homear.ui.screens.createProject

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.homear.domain.model.ProjectModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.usecase.proyect.SaveProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.*
import com.app.homear.core.utils.SharedPreferenceHelper
import com.app.homear.domain.model.SpaceModel
import com.app.homear.domain.repository.LocalStorageRepository
import com.app.homear.domain.usecase.space.SaveSpaceUseCase

data class Espacio(
    val nombre: String,
    val cantidadMuebles: Int,
    val imagePath: String? = null
)

@HiltViewModel
class CreateProjectViewModel @Inject constructor(
    private val saveProjectUseCase: SaveProjectUseCase,
    private val saveSpaceUseCase: SaveSpaceUseCase,
) : ViewModel() {
    
    private val _projectName = mutableStateOf("")
    val projectName = _projectName
    
    private val _projectDescription = mutableStateOf("")
    val projectDescription = _projectDescription
    
    private val _projectImagePath = mutableStateOf("")
    val projectImagePath = _projectImagePath
    
    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading
    
    private val _isProjectCreated = mutableStateOf(false)
    val isProjectCreated = _isProjectCreated
    
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage = _errorMessage
    
    private val _spacesList = mutableStateOf<List<Espacio>>(emptyList())
    val spacesList = _spacesList
    
    fun updateProjectName(context: Context,name: String) {
        _projectName.value = name
        saveProjectState(context) // Guardar automáticamente
    }
    
    fun updateProjectDescription(context: Context,description: String) {
        _projectDescription.value = description
        saveProjectState(context) // Guardar automáticamente
    }
    
    fun updateProjectImagePath(context: Context, imagePath: String) {
        _projectImagePath.value = imagePath
        saveProjectState(context) // Guardar automáticamente
    }
    
    fun createProject(context: Context, userId: String = "default_user") {
        if (_projectName.value.isBlank()) {
            _errorMessage.value = "El nombre del proyecto es requerido"
            return
        }
        
        _isLoading.value = true
        _errorMessage.value = null
        
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        
        val projectModel = ProjectModel(
            id = 0, // Se auto-genera en la base de datos
            idUser = userId,
            imagePath = _projectImagePath.value.ifBlank { "" },
            name = _projectName.value,
            description = _projectDescription.value.ifBlank { "Proyecto creado el $currentDate" },
            createdDate = currentDate,
            lastModified = currentDate
        )
        
        viewModelScope.launch {
            saveProjectUseCase(projectModel).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                    is Resource.Success -> {
                        _isLoading.value = false
                        _isProjectCreated.value = true
                        val projectId = resource.data?.toInt() ?: -1
                        if (projectId != -1) {
                            saveSpacesForProject(projectId, userId)
                        }
                        clearSavedProjectState(context) // Limpiar estado guardado
                        Log.d("CreateProjectViewModel", "Proyecto creado exitosamente con ID: ${resource.data}")
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = resource.message ?: "Error desconocido al crear el proyecto"
                        Log.e("CreateProjectViewModel", "Error al crear proyecto: ${resource.message}")
                    }
                }
            }
        }
    }

    private fun saveSpacesForProject(projectId: Int, userId: String) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        viewModelScope.launch {
        for (espacio in _spacesList.value) {
            val spaceModel = SpaceModel(
                id = 0, // Se auto-genera en la base de datos
                projectId = projectId,
                idUser = userId,
                name = espacio.nombre,
                description = "", // Puedes extender Espacio para incluir descripción si lo deseas
                imagePath = espacio.imagePath ?: "",
                createdDate = currentDate,
                lastModified = currentDate
            )
            saveSpaceUseCase(spaceModel).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                    is Resource.Success -> {
                        Log.d("CreateProjectViewModel", "Espacio guardado exitosamente")
                    }
                    is Resource.Error -> {
                        Log.e("CreateProjectViewModel", "Error al guardar espacio: ${resource.message}")
                    }
                }
            }
            }
        }

    }
    
    fun resetState(context: Context) {
        _projectName.value = ""
        _projectDescription.value = ""
        _projectImagePath.value = ""
        _isLoading.value = false
        _isProjectCreated.value = false
        _errorMessage.value = null
        _spacesList.value = emptyList()
        clearSavedProjectState(context) // Limpiar estado guardado
    }
    
    // Función para preservar el estado actual
    fun preserveState() {
        // No hacer nada, solo mantener el estado actual
    }
    
    // Función para limpiar solo el estado de creación, pero mantener los datos del formulario
    fun clearCreationState() {
        _isLoading.value = false
        _isProjectCreated.value = false
        _errorMessage.value = null
    }
    
    // Función para guardar el estado actual del proyecto
    fun saveProjectState(context: Context) {
        val sharedPrefHelper = SharedPreferenceHelper(context)
        sharedPrefHelper.saveStringData("temp_project_name", _projectName.value)
        sharedPrefHelper.saveStringData("temp_project_description", _projectDescription.value)
        sharedPrefHelper.saveStringData("temp_project_image_path", _projectImagePath.value)
    }
    
    // Función para restaurar el estado del proyecto
    fun restoreProjectState(context: Context) {
        val sharedPrefHelper = SharedPreferenceHelper(context)
        val savedName = sharedPrefHelper.getStringData("temp_project_name")
        val savedDescription = sharedPrefHelper.getStringData("temp_project_description")
        val savedImagePath = sharedPrefHelper.getStringData("temp_project_image_path")
        
        if (!savedName.isNullOrEmpty()) {
            _projectName.value = savedName
        }
        if (!savedDescription.isNullOrEmpty()) {
            _projectDescription.value = savedDescription
        }
        if (!savedImagePath.isNullOrEmpty()) {
            _projectImagePath.value = savedImagePath
        }
    }
    
    // Función para limpiar el estado temporal guardado
    fun clearSavedProjectState(context: Context) {
        val sharedPrefHelper = SharedPreferenceHelper(context)
        sharedPrefHelper.saveStringData("temp_project_name", null)
        sharedPrefHelper.saveStringData("temp_project_description", null)
        sharedPrefHelper.saveStringData("temp_project_image_path", null)
        clearSavedSpacesList(context)
    }
    
    // Función para agregar un espacio a la lista
    fun addSpace(context: Context, espacio: Espacio) {
        val currentList = _spacesList.value.toMutableList()
        currentList.add(espacio)
        _spacesList.value = currentList
        saveSpacesList(context)
    }
    
    // Función para guardar la lista de espacios
    private fun saveSpacesList(context: Context) {
        val sharedPrefHelper = SharedPreferenceHelper(context)
        val spacesJson = org.json.JSONArray().apply {
            _spacesList.value.forEach { espacio ->
                val obj = org.json.JSONObject()
                obj.put("nombre", espacio.nombre)
                obj.put("cantidadMuebles", espacio.cantidadMuebles)
                obj.put("imagePath", espacio.imagePath ?: "")
                put(obj)
            }
        }.toString()
        sharedPrefHelper.saveStringData("temp_project_spaces", spacesJson)
    }
    
    // Función para restaurar la lista de espacios
    fun restoreSpacesList(context: Context) {
        val sharedPrefHelper = SharedPreferenceHelper(context)
        val spacesJson = sharedPrefHelper.getStringData("temp_project_spaces")
        if (!spacesJson.isNullOrEmpty()) {
            try {
                val jsonArray = org.json.JSONArray(spacesJson)
                val spacesList = mutableListOf<Espacio>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    spacesList.add(
                        Espacio(
                            nombre = obj.getString("nombre"),
                            cantidadMuebles = obj.getInt("cantidadMuebles"),
                            imagePath = if (obj.getString("imagePath").isNotEmpty()) obj.getString("imagePath") else null
                        )
                    )
                }
                _spacesList.value = spacesList
            } catch (e: Exception) {
                Log.e("CreateProjectViewModel", "Error restaurando espacios: ${e.message}")
            }
        }
    }
    
    // Función para limpiar la lista de espacios guardada
    fun clearSavedSpacesList(context: Context) {
        val sharedPrefHelper = SharedPreferenceHelper(context)
        sharedPrefHelper.saveStringData("temp_project_spaces", null)
    }
} 