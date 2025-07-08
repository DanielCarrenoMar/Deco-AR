package com.app.homear.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.homear.domain.model.Superficie
import com.app.homear.domain.model.LocalFurnitureModel
import java.io.File
import java.util.HashSet

@Entity(tableName = "local_furniture")
data class LocalFurnitureEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "material") val material: String,
    @ColumnInfo(name = "keywords") val keywords: String,
    @ColumnInfo(name = "modelPath") val modelPath: String,
    @ColumnInfo(name = "imagePath") val imagePath: String,
    @ColumnInfo(name = "height") val height: Float,
    @ColumnInfo(name = "width") val width: Float,
    @ColumnInfo(name = "length") val length: Float,
    @ColumnInfo(name = "superficie") val superficie: Superficie,
    @ColumnInfo(name = "downloadDate") val downloadDate: String,
    @ColumnInfo(name = "isDownloaded") val isDownloaded: Boolean = true
)

fun LocalFurnitureEntity.toLocalFurnitureModel(): LocalFurnitureModel {
    return LocalFurnitureModel(
        id = this.id,
        name = this.name,
        description = this.description,
        material = HashSet(this.material.split(",").filter { it.isNotBlank() }),
        keywords = HashSet(this.keywords.split(",").filter { it.isNotBlank() }),
        modelFile = File(this.modelPath),
        imageFile = File(this.imagePath),
        height = this.height,
        width = this.width,
        length = this.length,
        superficie = this.superficie,
        downloadDate = this.downloadDate,
        isDownloaded = this.isDownloaded
    )
}

fun LocalFurnitureModel.toLocalFurnitureEntity(): LocalFurnitureEntity {
    return LocalFurnitureEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        material = this.material.joinToString(","),
        keywords = this.keywords.joinToString(","),
        modelPath = this.modelFile.path,
        imagePath = this.imageFile.path,
        height = this.height,
        width = this.width,
        length = this.length,
        superficie = this.superficie,
        downloadDate = this.downloadDate,
        isDownloaded = this.isDownloaded
    )
}