package com.example.parkingcompose.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.parkingcompose.model.LoginState
import com.example.parkingcompose.ui.theme.ButtonTextStyle
import com.example.parkingcompose.util.GoogleAuthUiClient
import com.example.parkingcompose.viewmodels.LoginMailViewModel
import com.example.parkingcompose.viewmodels.RegisterViewModel
import com.example.parkingcompose.R


@Composable
fun LoginScreen(
    navHostController: NavHostController,
    state: LoginState,
    loginViewModel: LoginMailViewModel,
    registerViewModel: RegisterViewModel,
    googleAuthUiClient: GoogleAuthUiClient,
    onLogin: (String, String) -> Unit,
    onRegister: () -> Unit,
    onSignInClick: () -> Unit
)  {
    val activity = LocalContext.current as ComponentActivity
    val loginState by loginViewModel.loginState.collectAsState()
    BackHandler {
        // Minimiza la aplicación
        activity.moveTaskToBack(true)
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(14.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.login_title),
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = loginState.email,
            onValueChange = { loginViewModel.onEmailChange(it) },
            label = { Text(stringResource(id = R.string.email)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = loginState.password,
            onValueChange = { loginViewModel.onPasswordChange(it) },
            label = { Text(stringResource(id = R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onLogin(loginState.email, loginState.password) },
            modifier = Modifier.fillMaxWidth().padding(0.dp, 0.dp, 0.dp, 8.dp)
        ) {
            Text(stringResource(id = R.string.login_button))
        }

        Button(
            onClick = { onSignInClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.google_sign_in))
        }

        Spacer(modifier = Modifier.height(65.dp))

        Button(
            onClick = { onRegister() },
            modifier = Modifier.fillMaxWidth(0.7F).padding(0.dp, 0.dp, 0.dp, 8.dp)
        ) {
            Text(stringResource(id = R.string.register))
        }

        Button(
            onClick = { navHostController.navigate("forgotpassword") },
            modifier = Modifier.fillMaxWidth(0.7F)
        ) {
            Text(stringResource(id = R.string.forgotpassword))
        }
    }
}

