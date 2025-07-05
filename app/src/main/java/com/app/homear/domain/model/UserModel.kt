package com.app.homear.domain.model

data class UserModel(
    val name: String,
    val email: String){
    companion object {
        val DEFAULT = UserModel(
            name = "Vacio",
            email = "Vacio"
        )
    }

}