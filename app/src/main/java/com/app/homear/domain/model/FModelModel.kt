package com.app.homear.domain.model

import com.app.homear.data.database.entity.FModelEntity
import java.io.File

data class FModelModel (
    val name: String,
    val description: String,
    val material: HashSet<String>,
    val keywords: HashSet<String>,
    val modelPath: File,
    val imagePath: File,
    val height: Float,
    val width: Float,
    val length: Float,
){
    companion object {
        val DEFAULT = FModelModel(
            name = "Vacio",
            description = "Vacio",
            material = HashSet(),
            keywords = HashSet(),
            modelPath = File(""),
            imagePath = File(""),
            height = 0f,
            width = 0f,
            length = 0f,
        )
    }
}

fun FModelModel.toFModelEntity(): FModelEntity{
    return FModelEntity(
        name = this.name,
        description = this.description,
        material = this.material,
        keywords = this.keywords,
        modelPath = this.modelPath,
        imagePath = this.imagePath,
        height = this.height,
        width = this.width,
        length = this.length,
    )
}