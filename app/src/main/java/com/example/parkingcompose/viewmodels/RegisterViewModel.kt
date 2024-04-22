package com.example.parkingcompose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.example.parkingcompose.dao.UserDao
import com.example.parkingcompose.model.Rol
import com.example.parkingcompose.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.parkingcompose.util.GoogleAuthUiClient


class RegisterViewModel : ViewModel() {
    val auth = Firebase.auth
    private val userDao = UserDao()
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

    fun registerUser(googleAuthUiClient: GoogleAuthUiClient) {
        UserDao().createUser(googleAuthUiClient)

    }
    fun registerUser() {
        if (password.value.text == repeatPassword.value.text && email.value.text.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email.value.text, password.value.text)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = task.result?.user?.uid
                        val user = User(
                            username = "",
                            email = email.value.text,
                            rol = Rol.User,
                            id = userId.toString()
                        ).toMap()
                        userDao.createUser(user)
                    } else {
                        // Aquí debes mostrar el mensaje de error
                    }
                }
        }
    }
}
