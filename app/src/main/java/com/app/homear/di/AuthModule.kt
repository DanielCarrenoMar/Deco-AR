package com.app.homear.di
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {
    @Singleton
    @Provides
    fun provideAuthClient(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}