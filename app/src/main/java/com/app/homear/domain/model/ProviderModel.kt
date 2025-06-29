package com.app.homear.domain.model

data class ProviderModel(
    val provider: String,
    val name: String,
    val id: String,
    val address: String,
    val phone: String,
    val email: String,
    val image: String,
    val description: String,
    val state : String,
    val city : String,
    val country : String
){
    companion object{
        val DEFAULT = ProviderModel(
            provider = "",
            name = "",
            id = "",
            address = "",
            phone = "",
            email = "",
            image = "",
            description = "",
            state = "",
            city = "",
            country = ""
        )
    }
}
