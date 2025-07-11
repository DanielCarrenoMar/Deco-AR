package com.app.homear.domain.model

data class SpaceFurnitureModel(
    val id: Int,
    val spaceId: Int,
    val name: String,
    val description: String,
    val modelPath: String,
    val imagePath: String,
)