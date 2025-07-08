package com.app.homear.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.app.homear.domain.model.ProjectModel

@Entity(
    tableName = "projects",
    foreignKeys = [
        ForeignKey(
            entity = SpaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["spaceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "idUser") val idUser: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "spaceId") val spaceId: Int,
    @ColumnInfo(name = "furnitureIds") val furnitureIds: String, // Lista de IDs separados por coma
    @ColumnInfo(name = "createdDate") val createdDate: String,
    @ColumnInfo(name = "lastModified") val lastModified: String,
    @ColumnInfo(name = "isCompleted") val isCompleted: Boolean = false
)

fun ProjectEntity.toProjectModel(): ProjectModel {
    return ProjectModel(
        id = this.id,
        idUser = this.idUser,
        name = this.name,
        description = this.description,
        spaceId = this.spaceId,
        furnitureIds = if (this.furnitureIds.isBlank()) emptyList() else this.furnitureIds.split(",")
            .mapNotNull { it.toIntOrNull() },
        createdDate = this.createdDate,
        lastModified = this.lastModified,
        isCompleted = this.isCompleted
    )
}

fun ProjectModel.toProjectEntity(): ProjectEntity {
    return ProjectEntity(
        id = this.id,
        idUser = this.idUser,
        name = this.name,
        description = this.description,
        spaceId = this.spaceId,
        furnitureIds = this.furnitureIds.joinToString(","),
        createdDate = this.createdDate,
        lastModified = this.lastModified,
        isCompleted = this.isCompleted
    )
}