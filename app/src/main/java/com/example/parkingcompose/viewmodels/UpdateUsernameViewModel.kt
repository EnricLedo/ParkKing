package com.example.parkingcompose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.parkingcompose.dao.UserDao
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class UpdateUsernameViewModel() : ViewModel() {
    val auth = Firebase.auth
    var username = mutableStateOf(TextFieldValue("")) // Cambia a variable mutable
    var currentUsername = mutableStateOf("")
    val userDao = UserDao()
    init {
        getCurrentUsername()
    }

    private fun getCurrentUsername() {
        val email = auth.currentUser?.email
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    currentUsername.value = document.getString("username") ?: ""
                }
            }
    }

    fun updateUsername() {
        if (username.value.text.isNotEmpty()) {
            val userId = auth.currentUser?.uid

            userDao.updateUsernameInDb(userId, username.value.text) {

                username.value = TextFieldValue("")
                getCurrentUsername()
            }
        }
    }
}