package com.app.homear.domain.model

data class SpaceModel(
    val projectId: Int,
    val idUser: String,
    val name: String,
    val description: String,
    val imagePath: String,
    val createdDate: String,
    val lastModified: String
)
