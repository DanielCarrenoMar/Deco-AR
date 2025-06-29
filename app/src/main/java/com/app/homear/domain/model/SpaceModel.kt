package com.app.homear.domain.model

data class SpaceModel(val idUser: String,
    val listFurniture: List<FurnitureModel>,
    val idImageGD: String
){
    companion object{
        val DEFAULT = SpaceModel(
            idUser = "",
            listFurniture = emptyList(),
            idImageGD = ""
        )
    }
}
