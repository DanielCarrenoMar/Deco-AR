package com.app.homear.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.app.homear.ui.screens.createspace.CreateSpaceViewModel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {
    @Provides
    @Singleton
    fun provideCreateSpaceViewModel(
        @ApplicationContext context: Context
    ): CreateSpaceViewModel {
        return CreateSpaceViewModel(context)
    }
} 