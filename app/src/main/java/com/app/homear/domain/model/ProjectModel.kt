package com.app.homear.domain.model

data class ProjectModel(
    val id: Int,
    val idUser: String,
    val imagePath: String,
    val name: String,
    val description: String,
    val createdDate: String,
    val lastModified: String,
)