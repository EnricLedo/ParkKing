package com.example.parkingcompose.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.parkingcompose.viewmodels.CreateParkingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CreateParkingScreen(viewModel: CreateParkingViewModel, navController: NavController) {

    val context = LocalContext.current
    //ESTO CREO QUE DEBERIA IR EN EL VIEWMODEL
    var image by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLancher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> image = uri }
    )
    //HASTA AQUI

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = viewModel.name.value,
            onValueChange = { viewModel.onNameChange(it) },
            label = { Text("Name") }
        )

        OutlinedTextField(
            value = viewModel.description.value,
            onValueChange = { viewModel.onDescriptionChange(it) },
            label = { Text("Description") },
        )

        OutlinedTextField(
            value = viewModel.priceMinute.value,
            onValueChange = { viewModel.onPriceMinuteChange(it) },
            label = { Text("Price Minute") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Button(onClick = {photoPickerLancher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
        }) {
            Text("Select Image")
        }

        if (image != null) {
            AsyncImage(
                model = image,
                contentDescription = null,
                modifier = Modifier.padding(10.dp).height(300.dp)
            )
        }

        Button(onClick = {
            if(image != null) {
                // Lanza una corrutina para llamar a onAddParking
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.onAddParking(context, image)
                }
            } else {
                Toast.makeText(
                    context,
                    "Please select an image -> CreateParkingScreen",
                    Toast.LENGTH_SHORT
                ).show()
            }
            navController.popBackStack()
        }) {
            Text("Add Parking")
        }

    }
}