package com.app.homear.domain.usecase.spaceFurniture

import com.app.homear.domain.model.Resource
import com.app.homear.domain.model.SpaceFurnitureModel
import com.app.homear.domain.model.SpaceModel
import com.app.homear.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetSpaceFurnituresBySpaceIdUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(spaceId: Int): Flow<Resource<List<SpaceFurnitureModel>>> = channelFlow {
        try {
            send(Resource.Loading())
            send(
                Resource.Success(
                    data = repository.getSpacesFurnitureBySpaceId(spaceId)
                )
            )
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}