package com.example.parkingcompose.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.parkingcompose.R
import com.example.parkingcompose.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(viewModel: ForgotPasswordViewModel) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    // Recursos de strings
    val strForgotPassword = stringResource(id = R.string.forgot_password)
    val strEmail = stringResource(id = R.string.email)
    val strSendResetEmail = stringResource(id = R.string.send_reset_email)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = strForgotPassword)

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { newEmail -> email = newEmail },
            label = { Text(strEmail) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // Llama a la función del ViewModel para enviar el correo de restablecimiento
            viewModel.sendPasswordResetEmail(email) { result ->
                message = result
            }
        }) {
            Text(strSendResetEmail)
        }

        message?.let { msg ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(msg)
        }
    }
}
