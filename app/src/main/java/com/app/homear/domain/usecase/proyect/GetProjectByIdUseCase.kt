package com.app.homear.domain.usecase.proyect

import com.app.homear.domain.model.ProjectModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProjectByIdUseCase @Inject constructor(
    private val localStorageRepository: LocalStorageRepository
) {
    operator fun invoke(projectId: Int): Flow<Resource<ProjectModel>> {
        return localStorageRepository.getProjectById(projectId)
    }
} 