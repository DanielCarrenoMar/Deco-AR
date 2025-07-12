package com.app.homear.domain.usecase.space

import com.app.homear.domain.model.SpaceModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSpaceByIdUseCase @Inject constructor(
    private val localStorageRepository: LocalStorageRepository
) {
    operator fun invoke(spaceId: Int): Flow<Resource<SpaceModel>> {
        return localStorageRepository.getSpaceById(spaceId)
    }
} 