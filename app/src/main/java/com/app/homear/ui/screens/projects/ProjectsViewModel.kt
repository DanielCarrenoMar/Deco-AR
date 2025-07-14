package com.app.homear.ui.screens.projects

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.homear.domain.model.ProjectModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.usecase.proyect.GetAllProyectUseCase
import com.app.homear.domain.usecase.proyect.SaveProjectUseCase
import com.app.homear.domain.repository.LocalStorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel  @Inject constructor(
    private val getAllProyectUseCase: GetAllProyectUseCase,
    private val saveProjectUseCase: SaveProjectUseCase,
    private val localStorageRepository: LocalStorageRepository
): ViewModel()  {
    private val _projectList = mutableStateOf<List<ProjectModel>>(emptyList())
    var projectList = _projectList

    fun loadProjects() {
        viewModelScope.launch {
            getAllProyectUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Success -> {
                        _projectList.value = resource.data!!
                    }
                    is Resource.Error -> {
                        Log.e("ProjectsViewModel", "Error loading projects: ${resource.message}")
                    }
                }
            }
        }
    }

    fun testSaveProyect(projectModel: ProjectModel){
        viewModelScope.launch {
            saveProjectUseCase(projectModel).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Success -> {
                        val idSaved = resource.data!!
                    }
                    is Resource.Error -> {
                        Log.e("ProjectsViewModel", "Error al guardar el proyecto: ${resource.message}")
                    }
                }
            }
        }
    }
    
    fun deleteProject(projectId: Int) {
        viewModelScope.launch {
            try {
                val success = localStorageRepository.deleteProjectFromId(projectId)
                if (success) {
                    Log.d("ProjectsViewModel", "Proyecto eliminado exitosamente")
                    loadProjects() // Recargar la lista
                } else {
                    Log.e("ProjectsViewModel", "Error al eliminar el proyecto")
                }
            } catch (e: Exception) {
                Log.e("ProjectsViewModel", "Error al eliminar proyecto: ${e.message}")
            }
        }
    }
}