package com.app.homear.domain.usecase.space

import com.app.homear.domain.model.Resource
import com.app.homear.domain.model.SpaceModel
import com.app.homear.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetSpacesByProjectIdUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(projectId: Int): Flow<Resource<List<SpaceModel>>> = channelFlow {
        try {
            send(Resource.Loading())
            send(
                Resource.Success(
                    data = repository.getSpacesByProjectId(projectId)
                )
            )
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}