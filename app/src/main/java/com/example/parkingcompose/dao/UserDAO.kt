package com.example.parkingcompose.dao

import android.util.Log
import com.example.parkingcompose.data.Rol
import com.example.parkingcompose.data.User
import com.example.parkingcompose.util.GoogleAuthUiClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class UserDao() {
    private val auth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()

    fun createUser(googleAuthUiClient: GoogleAuthUiClient) {
        val userId = auth.currentUser?.uid
        val email: String

        val googleUser = googleAuthUiClient.getSignedInUser()
        if (googleUser == null) {
            // Usuario ha iniciado sesión con email y contraseña
            email = auth.currentUser?.email.toString()
        } else {
            // Usuario ha iniciado sesión con Google
            email = googleUser.email ?: ""
        }

        // Comprueba si ya existe un usuario con el mismo correo electrónico
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No existe un usuario con el mismo correo electrónico, por lo que se puede crear uno nuevo
                    val user = User(
                        username = "",
                        email = email,
                        rol = Rol.User,
                        id = userId.toString()
                    ).toMap()

                    // Añade un nuevo documento a la colección "users"
                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }
                } else {
                    // Ya existe un usuario con el mismo correo electrónico
                    Log.w(TAG, "User with this email already exists")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error checking document", e)
            }
    }

    companion object {
        private const val TAG = "UserDao"
    }

    fun updateUsernameInDb(userId: String?, username: String, onSuccess: () -> Unit) {
        db.collection("users")
            .whereEqualTo("user_id", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.update("username", username)
                }
            }
            .addOnSuccessListener {
                onSuccess()
            }
    }
}

