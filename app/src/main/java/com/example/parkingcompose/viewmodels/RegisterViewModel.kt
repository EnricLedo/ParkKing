package com.example.parkingcompose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.example.parkingcompose.dao.UserDao
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.parkingcompose.util.GoogleAuthUiClient


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

    fun registerUser(googleAuthUiClient: GoogleAuthUiClient) {
        UserDao().createUser(googleAuthUiClient)

    }
}
