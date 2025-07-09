package com.app.homear.domain.model

data class UserModel(
    val name: String,
    val email: String,
    val type: String,
    val key: String ){
    companion object {
        val DEFAULT = UserModel(
            name = "Vacio",
            email = "Vacio",
            type = "",
            key = ""
        )
    }
}