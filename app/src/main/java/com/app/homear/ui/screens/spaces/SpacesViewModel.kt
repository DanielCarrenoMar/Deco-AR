package com.app.homear.ui.screens.spaces

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.homear.domain.model.Resource
import com.app.homear.domain.model.SpaceModel
import com.app.homear.domain.usecase.space.GetAllSpacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpacesViewModel  @Inject constructor(
    private val getAllSpacesUseCase: GetAllSpacesUseCase
): ViewModel()  {
    private val _projectList = mutableStateOf<List<SpaceModel>>(emptyList())
    var projectList = _projectList.value

    fun loadSpaces() {
        viewModelScope.launch {
            getAllSpacesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Success -> {
                        _projectList.value = resource.data!!
                    }
                    is Resource.Error -> {
                        // Handle error state if needed
                    }
                }
            }
        }
    }
}