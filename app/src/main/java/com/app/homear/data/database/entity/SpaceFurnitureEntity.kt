package com.app.homear.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.homear.domain.model.SpaceFurnitureModel

@Entity(tableName = "space_furniture")
data class SpaceFurnitureEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "spaceId") val spaceId: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "modelPath") val modelPath: String,
    @ColumnInfo(name = "imagePath") val imagePath: String,
)

fun SpaceFurnitureEntity.toSpaceFurnitureModel(): SpaceFurnitureModel {
    return SpaceFurnitureModel(
        id = this.id,
        spaceId = this.spaceId,
        name = this.name,
        description = this.description,
        modelPath = this.modelPath,
        imagePath = this.imagePath,
    )
}

fun SpaceFurnitureModel.toSpaceFurnitureEntity(): SpaceFurnitureEntity {
    return SpaceFurnitureEntity(
        id = this.id,
        spaceId = this.spaceId,
        name = this.name,
        description = this.description,
        modelPath = this.modelPath,
        imagePath = this.imagePath,
    )
}