package com.app.homear.ui.screens.projects

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.homear.domain.model.ProjectModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.usecase.proyect.GetAllProyectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel  @Inject constructor(
    private val getAllProyectUseCase: GetAllProyectUseCase
): ViewModel()  {
    private val _projectList = mutableStateOf<List<ProjectModel>>(emptyList())
    var projectList = _projectList.value

    fun loadProjects() {
        viewModelScope.launch {
            getAllProyectUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Success -> {
                        _projectList.value = resource.data!!
                        Log.i("ProjectsViewModel", "Projects loaded successfully: ${_projectList.value.size} projects found.")
                    }
                    is Resource.Error -> {
                        // Handle error state if needed
                    }
                }
            }
        }
    }
}