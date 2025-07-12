package com.app.homear.ui.screens.spaceDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.homear.domain.model.SpaceFurnitureModel
import com.app.homear.domain.model.SpaceModel
import com.app.homear.domain.usecase.space.GetSpaceByIdUseCase
import com.app.homear.domain.usecase.spaceFurniture.GetSpaceFurnituresBySpaceIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpaceDetailViewModel @Inject constructor(
    private val getSpaceByIdUseCase: GetSpaceByIdUseCase,
    private val getSpaceFurnituresBySpaceIdUseCase: GetSpaceFurnituresBySpaceIdUseCase
) : ViewModel() {
    private val _space = MutableLiveData<SpaceModel?>(null)
    val space: LiveData<SpaceModel?> = _space

    private val _furnitureList = MutableLiveData<List<SpaceFurnitureModel>>(emptyList())
    val furnitureList: LiveData<List<SpaceFurnitureModel>> = _furnitureList

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadSpace(spaceId: Int) {
        viewModelScope.launch {
            getSpaceByIdUseCase(spaceId).collect { resource ->
                when (resource) {
                    is com.app.homear.domain.model.Resource.Loading -> {
                        _isLoading.value = true
                        _error.value = null
                    }
                    is com.app.homear.domain.model.Resource.Success -> {
                        _isLoading.value = false
                        _space.value = resource.data
                    }
                    is com.app.homear.domain.model.Resource.Error -> {
                        _isLoading.value = false
                        _error.value = resource.message
                    }
                }
            }
        }
    }

    fun loadFurniture(spaceId: Int) {
        viewModelScope.launch {
            getSpaceFurnituresBySpaceIdUseCase(spaceId-1).collect { resource ->
                when (resource) {
                    is com.app.homear.domain.model.Resource.Loading -> {
                        _isLoading.value = true
                        _error.value = null
                    }
                    is com.app.homear.domain.model.Resource.Success -> {
                        _isLoading.value = false
                        _furnitureList.value = resource.data!!
                    }
                    is com.app.homear.domain.model.Resource.Error -> {
                        _isLoading.value = false
                        _error.value = resource.message
                    }
                }
            }
        }
    }
}