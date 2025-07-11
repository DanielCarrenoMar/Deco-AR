package com.app.homear.ui.screens.createspace

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import android.os.Environment
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.io.File

@HiltViewModel
class CreateSpaceViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _imagePath = mutableStateOf<String?>(null)
    val imagePath: State<String?> = _imagePath

    init {
        // Al iniciar, buscar la última imagen guardada
        _imagePath.value = findLastImage()
    }

    private fun findLastImage(): String? {
        val directory = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DecorAR")
        } else {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/DecorAR")
        }

        return directory.listFiles()
            ?.filter { it.extension.lowercase() == "jpg" }
            ?.maxByOrNull { it.lastModified() }
            ?.absolutePath
    }
} 