package com.example.parkingcompose.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.parkingcompose.viewmodels.UpdateUsernameViewModel

@Composable
fun UpdateUsernameScreen(viewModel: UpdateUsernameViewModel, navHostController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tu actual username es:",
            fontSize = 28.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "${viewModel.currentUsername.value}",
            fontSize = 28.sp,
            color = Color.Green,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Escribe el nombre con el que quieres que te vean los demás",
            fontSize = 28.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.username.value,
            onValueChange = { viewModel.username.value = it },
            label = { Text("Nombre de usuario") }, // Cambia la etiqueta a "Nombre de usuario"
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.updateUsername()
                        navHostController.navigate("profile") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("SAVE")
        }
    }
}