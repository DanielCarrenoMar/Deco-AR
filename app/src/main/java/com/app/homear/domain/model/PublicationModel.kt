package com.app.homear.domain.model

data class PublicationModel(
    val idUser: String,
    val idPublication: String,
    val title: String,
    val description: String,
    val image: String,
    val date: String,
    val furnitureList: List<String>
){
    companion object {
        val empty = PublicationModel(
            idUser = "",
            idPublication = "",
            title = "",
            description = "",
            image = "",
            date = "",
            furnitureList = emptyList()
        )
    }
}
