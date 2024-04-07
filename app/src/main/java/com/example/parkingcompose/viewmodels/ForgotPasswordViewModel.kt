package com.example.parkingcompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ForgotPasswordViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    // Esta función se puede llamar desde la pantalla y manejará la lógica de enviar el correo de restablecimiento
    fun sendPasswordResetEmail(email: String, onResult: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                auth.sendPasswordResetEmail(email).await()
                // Notificar al observador del resultado exitoso
                onResult("Se ha enviado un correo de restablecimiento a $email")
            } catch (exception: Exception) {
                // Manejar las excepciones
                val errorMessage = when (exception) {
                    is FirebaseAuthInvalidUserException -> "No se encontró ninguna cuenta con el correo $email"
                    else -> "Ocurrió un error al enviar el correo de restablecimiento"
                }
                // Notificar al observador del resultado con el mensaje de error
                onResult(errorMessage)
            }
        }
    }
}