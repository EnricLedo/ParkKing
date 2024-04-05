package com.example.parkingcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException



@Composable
fun ForgotPasswordScreen() {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Forgot Password")

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { newEmail -> email = newEmail },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // Verificar si el correo electrónico existe antes de enviar el correo de restablecimiento
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    // El correo de restablecimiento se envió correctamente
                    message = "Se ha enviado un correo de restablecimiento a $email"
                }
                .addOnFailureListener { exception ->
                    if (exception is FirebaseAuthInvalidUserException) {
                        // El correo electrónico no está registrado en Firebase
                        message = "No se encontró ninguna cuenta con el correo $email"
                    } else {
                        // Ocurrió un error desconocido
                        message = "Ocurrió un error al enviar el correo de restablecimiento"
                    }
                }
        }) {
            Text("Enviar correo de restablecimiento")
        }

        message?.let { msg ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(msg)
        }
    }
}