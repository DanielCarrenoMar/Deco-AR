package com.app.homear.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.homear.domain.model.SpaceModel
import java.io.File

@Entity(tableName = "spaces")
data class SpaceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "projectId") val projectId: Int,
    @ColumnInfo(name = "idUser") val idUser: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "imagePath") val imagePath: String,
    @ColumnInfo(name = "createdDate") val createdDate: String,
    @ColumnInfo(name = "lastModified") val lastModified: String,
)

fun SpaceEntity.toSpaceModel(): SpaceModel {
    return SpaceModel(
        id = this.id,
        projectId = this.projectId,
        idUser = this.idUser,
        name = this.name,
        description = this.description,
        imagePath = this.imagePath,
        createdDate = this.createdDate,
        lastModified = this.lastModified
    )
}

fun SpaceModel.toSpaceEntity(): SpaceEntity {
    return SpaceEntity(
        projectId = this.projectId,
        idUser = this.idUser,
        name = this.name,
        description = this.description,
        imagePath = this.imagePath,
        createdDate = this.createdDate,
        lastModified = this.lastModified,
    )
}