package com.app.homear.data.firebase

import com.app.homear.domain.model.UserModel

/**
 * Entidad utilizada exclusivamente para la deserialización de documentos Firestore
 * que representan un usuario.  Debe tener un constructor sin argumentos y valores
 * por defecto para que Firestore pueda instanciarla a través de reflection.
 */
data class FirestoreUserEntity(
    val name: String = "",
    val email: String = "",
    val type: String = "",
    val key: String = ""
) {
    fun toUserModel(): UserModel {
        return UserModel(
            name = this.name,
            email = this.email,
            type = this.type,
            key = this.key
        )
    }
}
