package com.example.parkingcompose.viewmodels

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.parkingcompose.MainActivity
import com.example.parkingcompose.data.SignInResult
import com.example.parkingcompose.data.LoginState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginMailViewModel: ViewModel() {
    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    private val _signInState = MutableStateFlow(LoginState())
    val signInState = _signInState.asStateFlow()

    fun onEmailChange(email: String) {
        _loginState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _loginState.update { it.copy(password = password) }
    }

    fun login(context: Context, email: String, password: String) {
        val trimmedEmail = email.trimEnd()
        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(trimmedEmail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso, puedes iniciar tu actividad aquí si lo necesitas
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                } else {
                    // El inicio de sesión falló, maneja el error aquí
                    val message = task.exception?.message
                    Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun onSignInResult(result: SignInResult) {
        _signInState.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }

    fun resetState() {
        _signInState.update { LoginState() }
        _loginState.update { LoginState() }
    }
}