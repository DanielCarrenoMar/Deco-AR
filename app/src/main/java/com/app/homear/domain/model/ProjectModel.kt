package com.app.homear.domain.model

data class ProjectModel(
    val id: Int = 0,
    val idUser: String,
    val name: String,
    val description: String,
    val spaceId: Int,
    val furnitureIds: List<Int>,
    val createdDate: String,
    val lastModified: String,
    val isCompleted: Boolean = false
) {
    companion object {
        val DEFAULT = ProjectModel(
            id = 0,
            idUser = "",
            name = "",
            description = "",
            spaceId = 0,
            furnitureIds = emptyList(),
            createdDate = "",
            lastModified = "",
            isCompleted = false
        )
    }
}