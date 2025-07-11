package com.app.homear.domain.model

import java.io.File

data class SpaceModel(
    val id: Int = 0,
    val idUser: String,
    val name: String,
    val description: String,
    val listFurniture: List<Int>,
    val imagePath: String,
    val createdDate: String,
    val lastModified: String
) {
    companion object {
        val DEFAULT = SpaceModel(
            id = 0,
            idUser = "",
            name = "",
            description = "",
            listFurniture = emptyList(),
            imagePath = "",
            createdDate = "",
            lastModified = ""
        )
    }
}
