package com.example.parkingcompose.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.parkingcompose.R
import com.example.parkingcompose.dao.ParkingDAO
import com.example.parkingcompose.ui.theme.ButtonTextStyle
import com.example.parkingcompose.ui.theme.Orange
import com.example.parkingcompose.ui.theme.OrangeDark
import com.example.parkingcompose.viewmodels.UpdateUsernameViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateUsernameScreen(viewModel: UpdateUsernameViewModel, parkingDAO: ParkingDAO, navController: NavHostController) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.your_current_username_is),
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 16.dp)
        )
        Text(

            text = stringResource(R.string.enter_your_new_username),
            fontSize = 28.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.username.value,
            onValueChange = { viewModel.username.value = it },
            label = { Text(stringResource(id = (R.string.usernames))) }, // Cambia la etiqueta a "Nombre de usuario"
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 6.dp, 0.dp, 36.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                cursorColor = Color.White,
                focusedLabelColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                unfocusedLabelColor = Color.White
            )

        )

        Button(
            onClick = {
            viewModel.viewModelScope.launch {
            val oldUsername = viewModel.currentUsername.value
            val newUsername = viewModel.username.value.text // Obtén el nuevo nombre de usuario antes de llamar a updateUsername()
            val isUpdated = viewModel.updateUsername()
            if (isUpdated) {
                parkingDAO.updateCreatedBy(oldUsername, newUsername)
                Toast.makeText(context,
                    context.getString(R.string.nombre_de_usuario_cambiado), Toast.LENGTH_SHORT).show()
                navController.navigate("profile")
            } else {
                Toast.makeText(context,
                    context.getString(R.string.el_nombre_de_usuario_ya_est_en_uso), Toast.LENGTH_SHORT).show()
            }
        }
    },
    modifier = Modifier.fillMaxWidth()
) {
    Text(stringResource(R.string.save), style = ButtonTextStyle)
}
    }
}