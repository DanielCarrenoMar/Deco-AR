package com.app.homear.domain.usecase.firestore

import com.app.homear.domain.model.PublicationModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.FirebaseStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetPublicationsUseCase @Inject constructor(
    private val repository: FirebaseStorageRepository
){
    operator fun invoke(): Flow<Resource<List<PublicationModel>>> = channelFlow {
        send(Resource.Loading())
        try {
            val publications = repository.getPublications()
            send(Resource.Success(data = publications))
        } catch (exception: Exception) {
            send(Resource.Error(exception.message ?: "Unknown Error"))
        }
    }
}