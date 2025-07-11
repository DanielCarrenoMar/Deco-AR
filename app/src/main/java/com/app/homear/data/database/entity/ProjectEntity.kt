package com.app.homear.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.app.homear.domain.model.ProjectModel

@Entity(tableName = "projects",)
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "idUser") val idUser: String,
    @ColumnInfo(name = "imagePath") val imagePath: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "createdDate") val createdDate: String,
    @ColumnInfo(name = "lastModified") val lastModified: String,
    @ColumnInfo(name = "isCompleted") val isCompleted: Boolean = false
)

fun ProjectEntity.toProjectModel(): ProjectModel {
    return ProjectModel(
        idUser = this.idUser,
        imagePath = this.imagePath,
        name = this.name,
        description = this.description,
        createdDate = this.createdDate,
        lastModified = this.lastModified,
        isCompleted = this.isCompleted
    )
}

fun ProjectModel.toProjectEntity(): ProjectEntity {
    return ProjectEntity(
        idUser = this.idUser,
        imagePath = this.imagePath,
        name = this.name,
        description = this.description,
        createdDate = this.createdDate,
        lastModified = this.lastModified,
        isCompleted = this.isCompleted
    )
}