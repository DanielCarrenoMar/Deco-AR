package com.app.homear.domain.model

import java.io.File

data class LocalFurnitureModel(
    val id: Int = 0,
    val name: String,
    val description: String,
    val material: HashSet<String>,
    val keywords: HashSet<String>,
    val modelFile: File,
    val imageFile: File,
    val height: Float,
    val width: Float,
    val length: Float,
    val superficie: Superficie,
    val downloadDate: String,
    val isDownloaded: Boolean = true
) {
    companion object {
        val DEFAULT = LocalFurnitureModel(
            id = 0,
            name = "",
            description = "",
            material = HashSet(),
            keywords = HashSet(),
            modelFile = File(""),
            imageFile = File(""),
            height = 0f,
            width = 0f,
            length = 0f,
            superficie = Superficie.TODAS,
            downloadDate = "",
            isDownloaded = false
        )
    }
}