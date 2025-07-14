package com.app.homear.domain.usecase.proyect

import android.util.Log
import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.ProjectModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class SaveProjectUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(projectModel: ProjectModel): Flow<Resource<Long>> = channelFlow {
        try {
            send(Resource.Loading())
            val data = repository.saveProject(projectModel)
            if (data.toInt() != -1){
                send(
                    Resource.Success(data = data)
                )
                Log.i("SaveProjectUseCase", "project guardado")
            } else {
                send(
                    Resource.Error("Save project Error")
                )
            }
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}