package com.app.homear.data.database.entity

import android.health.connect.datatypes.units.Length
import android.icu.text.ListFormatter.Width
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fmodel")
data class FModelEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "material") val material: String,
    @ColumnInfo(name = "keywords") val keywords: String,
    @ColumnInfo(name = "modelPath") val modelPath: String,
    @ColumnInfo(name = "imagePath") val imagePath: String,
    @ColumnInfo(name = "heigth") val heigth: Float,
    @ColumnInfo(name = "width") val width: Float,
    @ColumnInfo(name = "length") val length: Float,
)
