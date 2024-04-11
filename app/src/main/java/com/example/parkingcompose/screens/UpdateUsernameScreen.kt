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
import com.example.parkingcompose.ui.theme.ButtonTextStyle
import com.example.parkingcompose.ui.theme.Orange
import com.example.parkingcompose.ui.theme.OrangeDark
import com.example.parkingcompose.ui.theme.OrangeLight
import com.example.parkingcompose.viewmodels.UpdateUsernameViewModel

@Composable
fun UpdateUsernameScreen(viewModel: UpdateUsernameViewModel, navHostController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Your current username is",
            fontSize = 28.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "${viewModel.currentUsername.value}",
            fontSize = 28.sp,
            color = Orange,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(0.dp, 0.dp, 0.dp, 16.dp)
        )
        Text(
            text = "Enter your new username:",
            fontSize = 28.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.username.value,
            onValueChange = { viewModel.username.value = it },
            label = { Text("Username", color = OrangeDark) }, // Cambia la etiqueta a "Nombre de usuario"
            modifier = Modifier.fillMaxWidth().padding(0.dp, 6.dp, 0.dp, 36.dp)

        )

        Button(
            onClick = { viewModel.updateUsername()
                        navHostController.navigate("profile") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("SAVE", style = ButtonTextStyle)
        }
    }
}