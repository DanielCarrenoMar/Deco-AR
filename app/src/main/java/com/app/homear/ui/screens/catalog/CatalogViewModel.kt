package com.app.homear.ui.screens.catalog

import android.content.Context
import android.os.Environment
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.homear.domain.model.Resource
import com.app.homear.domain.usecase.GetModelFilesFromDirUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getModelFilesFromDirUseCase: GetModelFilesFromDirUseCase
): ViewModel(){
    private val _modelUris = mutableStateOf<List<File>>(emptyList())
    val modelUris = _modelUris

    fun getAllModelFiles(context: Context){
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
        if (dir == null) return
        viewModelScope.launch {
            getModelFilesFromDirUseCase(dir).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Success -> {
                        _modelUris.value = result.data!!
                    }
                    is Resource.Error -> {
                        // Handle error state if needed
                    }
                }
            }
        }
    }
}