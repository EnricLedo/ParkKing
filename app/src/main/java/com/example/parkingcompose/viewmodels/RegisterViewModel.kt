package com.example.parkingcompose.viewmodels

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.parkingcompose.data.Rol
import com.example.parkingcompose.data.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import androidx.navigation.NavHostController


class RegisterViewModel : ViewModel() {
    val auth = Firebase.auth

    var email = mutableStateOf(TextFieldValue(""))
    var password = mutableStateOf(TextFieldValue(""))
    var repeatPassword = mutableStateOf(TextFieldValue(""))

    fun onEmailChange(newValue: TextFieldValue) {
        email.value = newValue
    }

    fun onPasswordChange(newValue: TextFieldValue) {
        password.value = newValue
    }

    fun onRepeatPasswordChange(newValue: TextFieldValue) {
        repeatPassword.value = newValue
    }

    fun register(navController: NavController) {
        if (password.value.text == repeatPassword.value.text && email.value.text.isNotEmpty() && password.value.text.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email.value.text, password.value.text)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val displayName = task.result.user?.email?.split("@")?.get(0)
                        createUser(displayName.toString())
                        navController.navigate("sign_in")
                    } else {
                        // Aquí debes mostrar el mensaje de error
                    }
                }
        }
    }

    private fun createUser(displayName: String){
        val userId = auth.currentUser?.uid

        val user = User(
            username = "",
            email = auth.currentUser?.email.toString(),
            rol = Rol.User,
            id = userId.toString()

        ).toMap()
        val db = FirebaseFirestore.getInstance()

        // Añade un nuevo documento a la colección "users"
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}