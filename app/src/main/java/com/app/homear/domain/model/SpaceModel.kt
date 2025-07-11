package com.app.homear.domain.model

data class SpaceModel(
    val id: Int = 0,
    val idUser: String,
    val name: String,
    val description: String,
    val listFurniture: List<Int>,
    val imagePath: String,
    val createdDate: String,
    val lastModified: String
)
