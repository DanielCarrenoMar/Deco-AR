package com.app.homear.di

import com.app.homear.BuildConfig
import com.app.homear.data.remoteStorage.DriveApiService
import com.app.homear.data.remoteStorage.RemoteStorageRepositoryImpl
import com.app.homear.domain.repository.RemoteStorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DriveApiModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideDriveApiService(retrofit: Retrofit): DriveApiService =
        retrofit.create(DriveApiService::class.java)

    @Provides
    @Singleton
    @Named("driveApiKey")
    fun provideDriveApiKey(): String = BuildConfig.API_GOOGLE_DRIVE

    @Provides
    @Singleton
    fun provideRemoteStorageRepository(
        driveApiService: DriveApiService,
        @Named("driveApiKey") apiKey: String
    ): RemoteStorageRepository =
        RemoteStorageRepositoryImpl(driveApiService, apiKey)
}
