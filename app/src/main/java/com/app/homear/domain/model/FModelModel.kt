package com.app.homear.domain.model

import com.app.homear.data.database.entity.FModelEntity
import java.io.File

/**
 * File Model Model contiene la informacion que se mostrara de los modelso 3D
 */
public final enum class Superficie(){
    PISO,
    TECHO,
    PARED,
    TODAS
}

data class FModelModel (
    val name: String,
    val description: String,
    val material: HashSet<String>,
    val keywords: HashSet<String>,
    val modelFile: File,
    val imageFile: File,
    val height: Float,
    val width: Float,
    val length: Float,
    val superficie: Superficie
    ){
    companion object {
        val DEFAULT = FModelModel(
            name = "Vacio",
            description = "Vacio",
            material = HashSet(),
            keywords = HashSet(),
            modelFile = File(""),
            imageFile = File(""),
            height = 0f,
            width = 0f,
            length = 0f,
            superficie = Superficie.TODAS
        )
    }
}

fun FModelModel.toFModelEntity(): FModelEntity{
    return FModelEntity(
        name = this.name,
        description = this.description,
        material = this.material.joinToString(","),
        keywords = this.keywords.joinToString(","),
        modelPath = this.modelFile.path,
        imagePath = this.imageFile.path,
        height = this.height,
        width = this.width,
        length = this.length,
        superficie = superficie
    )
}