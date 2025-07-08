package com.app.homear.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.homear.domain.model.SpaceModel

@Entity(tableName = "spaces")
data class SpaceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "idUser") val idUser: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "idImageGD") val idImageGD: String,
    @ColumnInfo(name = "createdDate") val createdDate: String,
    @ColumnInfo(name = "lastModified") val lastModified: String
)

fun SpaceEntity.toSpaceModel(): SpaceModel {
    return SpaceModel(
        id = this.id,
        idUser = this.idUser,
        name = this.name,
        description = this.description,
        listFurniture = emptyList(), // Se debe cargar por separado
        idImageGD = this.idImageGD,
        createdDate = this.createdDate,
        lastModified = this.lastModified
    )
}

fun SpaceModel.toSpaceEntity(): SpaceEntity {
    return SpaceEntity(
        id = this.id,
        idUser = this.idUser,
        name = this.name,
        description = this.description,
        idImageGD = this.idImageGD,
        createdDate = this.createdDate,
        lastModified = this.lastModified
    )
}