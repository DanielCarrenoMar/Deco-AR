package com.app.homear.di

import com.app.homear.BuildConfig
import com.app.homear.data.remoteStorage.DriveApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

@Module
@InstallIn(SingletonComponent::class)
object DriveApiModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(@Named("driveApiKey") apiKey: String): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val originalUrl = original.url
                val url = originalUrl.newBuilder()
                    .addQueryParameter("key", apiKey)
                    .build()
                val requestBuilder = original.newBuilder().url(url)
                chain.proceed(requestBuilder.build())
            }
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideDriveApiService(retrofit: Retrofit): DriveApiService =
        retrofit.create(DriveApiService::class.java)

    @Provides
    @Singleton
    @Named("driveApiKey")
    fun provideDriveApiKey(): String = BuildConfig.API_GOOGLE_DRIVE
}
