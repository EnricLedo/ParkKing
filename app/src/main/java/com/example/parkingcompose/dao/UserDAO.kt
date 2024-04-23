package com.example.parkingcompose.dao

import android.util.Log
import com.example.parkingcompose.model.Rol
import com.example.parkingcompose.model.User
import com.example.parkingcompose.util.GoogleAuthUiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

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


    fun createUser(user: Map<String, Any>) {
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    companion object {
        private const val TAG = "UserDao"
    }

    suspend fun updateUsernameInDb(userId: String?, newUsername: String): Boolean {
    val db = FirebaseFirestore.getInstance()
    var isUpdated = false

    try {
        val documents = db.collection("users")
            .whereEqualTo("user_id", userId)
            .get()
            .await()

        for (document in documents) {
            document.reference.update("username", newUsername).await()
        }
        isUpdated = true
    } catch (e: Exception) {
        isUpdated = false
    }
    return isUpdated
}

    fun getCurrentUsername(onSuccess: (String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        db.collection("users")
            .whereEqualTo("user_id", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val username = document.getString("username")
                    if (username != null) {
                        onSuccess(username)
                    }
                }
            }
    }



    suspend fun checkUsernameAvailable(username: String): Boolean {
        val db = FirebaseFirestore.getInstance()
        var isAvailable = false

        val task = db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .await()

        isAvailable = task.isEmpty

        return isAvailable
    }
}

