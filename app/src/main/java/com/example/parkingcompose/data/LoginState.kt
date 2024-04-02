package com.example.parkingcompose.data

data class LoginState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val email: String = "",
    val password: String = ""
)
