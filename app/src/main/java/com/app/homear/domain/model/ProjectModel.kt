package com.app.homear.domain.model

data class ProjectModel(
    val id: Int = 0,
    val idUser: String,
    val name: String,
    val description: String,
    val createdDate: String,
    val lastModified: String,
    val isCompleted: Boolean = false
)