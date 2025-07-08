package com.app.homear.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.app.homear.domain.model.Superficie

@Entity(
    tableName = "camera_storage",
    foreignKeys = [
        ForeignKey(
            entity = FurnitureEntity::class,
            parentColumns = ["id"],
            childColumns = ["furnitureId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CameraStorageEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "furnitureId") val furnitureId: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "material") val material: String,
    @ColumnInfo(name = "keywords") val keywords: String,
    @ColumnInfo(name = "modelPath") val modelPath: String,
    @ColumnInfo(name = "imagePath") val imagePath: String,
    @ColumnInfo(name = "height") val height: Float,
    @ColumnInfo(name = "width") val width: Float,
    @ColumnInfo(name = "length") val length: Float,
    @ColumnInfo(name = "superficie") val superficie: Superficie
)