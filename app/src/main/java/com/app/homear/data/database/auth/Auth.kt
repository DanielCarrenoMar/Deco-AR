package com.app.homear.data.database.auth

import com.google.firebase.auth.FirebaseAuth

class Auth{

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser


    fun signIn(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun signUp(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e -> onFailure(e) }
    }


    fun isSignedIn(): Boolean {
        return currentUser != null
    }
}