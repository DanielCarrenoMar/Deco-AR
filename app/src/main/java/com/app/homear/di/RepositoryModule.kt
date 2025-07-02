package com.app.homear.di

import com.app.homear.data.database.repository.LocalStorageRepositoryImpl
import com.app.homear.data.firebase.FirebaseStorageRepositoryImpl
import com.app.homear.data.remoteStorage.RemoteStorageRepositoryImpl
import com.app.homear.domain.repository.FirebaseStorageRepository
import com.app.homear.domain.repository.LocalStorageRepository
import com.app.homear.domain.repository.RemoteStorageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindLocalStorageRepository(
        localStorageRepositoryImpl: LocalStorageRepositoryImpl
    ): LocalStorageRepository

    @Singleton
    @Binds
    abstract fun bindFirebaseStoreRepository(
        firebaseStorageRepositoryImpl: FirebaseStorageRepositoryImpl
    ): FirebaseStorageRepository

    @Singleton
    @Binds
    abstract fun bindRemoteStorageRepository(
        remoteStorageRepositoryImpl: RemoteStorageRepositoryImpl,
    ): RemoteStorageRepository


}