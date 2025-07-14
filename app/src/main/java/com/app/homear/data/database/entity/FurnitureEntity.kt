package com.app.homear.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.homear.domain.model.FurnitureModel
import java.io.File
import com.app.homear.domain.model.Superficie

@Entity(tableName = "furniture")
data class FurnitureEntity(
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
    @ColumnInfo(name = "superficie") val superficie: Superficie
)

fun FurnitureEntity.toFurnitureModel(): FurnitureModel {
    return FurnitureModel(
        name = this.name,
        description = this.description,
        materials = HashSet(this.material.split(",").filter { it.isNotBlank() }),
        keywords = HashSet(this.keywords.split(",").filter { it.isNotBlank() }),
        modelFile = File(this.modelPath),
        imageFile = File(this.imagePath),
        height = this.height,
        width = this.width,
        length = this.length,
        superficie = this.superficie
    )
}

fun FurnitureModel.toFurnitureEntity(): FurnitureEntity {
    return FurnitureEntity(
        id = 0,
        name = this.name,
        description = this.description,
        material = this.materials.joinToString(","),
        keywords = this.keywords.joinToString(","),
        modelPath = this.modelFile.path,
        imagePath = this.imageFile.path,
        height = this.height,
        width = this.width,
        length = this.length,
        superficie = this.superficie
    )
}
