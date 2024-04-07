package com.example.parkingcompose.screens

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.parkingcompose.viewmodels.CrearParkingViewModel

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CrearParkingScreen(viewModel: CrearParkingViewModel) {

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = viewModel.name.value,
            onValueChange = { viewModel.onNameChange(it) },
            label = { Text("Name") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { /* Handle next key event */ })
        )

        OutlinedTextField(
            value = viewModel.description.value,
            onValueChange = { viewModel.onDescriptionChange(it) },
            label = { Text("Description") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { /* Handle next key event */ })
        )

        Button(onClick = { viewModel.onSelectImage(context) }) {
            Text("Select Image")
        }

        viewModel.image.value?.let {
            Image(bitmap = it, contentDescription = "Selected Image")
        }

        OutlinedTextField(
            value = viewModel.priceMinute.value,
            onValueChange = { viewModel.onPriceMinuteChange(it) },
            label = { Text("Price Minute") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { /* Handle done key event */ })
        )
        Button(onClick = { viewModel.onAddParking(context) }) {
            Text("Add Parking")
        }
    }
}