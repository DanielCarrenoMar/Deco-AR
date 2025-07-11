package com.app.homear.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.homear.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
                appContext,
                AppDatabase::class.java, "grader_database"
            ).fallbackToDestructiveMigration(true).build()
    }

    @Singleton
    @Provides
    fun provideFurnitureDao(db: AppDatabase) = db.getFurnitureDao()

    @Singleton
    @Provides
    fun provideSpaceDao(db: AppDatabase) = db.getSpaceDao()

    @Singleton
    @Provides
    fun provideProjectDao(db: AppDatabase) = db.getProjectDao()

    @Singleton
    @Provides
    fun provideSpaceFurnitureDao(db: AppDatabase) = db.getSpaceFurnitureDao()
}