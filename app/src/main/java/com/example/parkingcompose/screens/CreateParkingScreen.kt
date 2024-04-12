package com.example.parkingcompose.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.parkingcompose.viewmodels.CreateParkingViewModel
import com.example.parkingcompose.viewmodels.SelectLocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CreateParkingScreen(
    createParkingViewModel: CreateParkingViewModel,
    selectLocationViewModel: SelectLocationViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val selectedLocation by selectLocationViewModel.selectedLocation.collectAsState()

    // Actualiza la latitud y longitud cuando la ubicación seleccionada cambie
    createParkingViewModel.latitude.value = selectedLocation?.latitude ?: 0.0
    createParkingViewModel.longitude.value = selectedLocation?.longitude ?: 0.0

    // Para seleccionar una imagen
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> createParkingViewModel.selectedImage.value = uri }
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Crear Parking",
                fontSize = 28.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = createParkingViewModel.name.value,
                onValueChange = { createParkingViewModel.onNameChange(it) },
                label = { Text("Nombre del Parking") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = createParkingViewModel.description.value,
                onValueChange = { createParkingViewModel.onDescriptionChange(it) },
                label = { Text("Descripción del Parking") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = createParkingViewModel.priceMinute.value,
                onValueChange = { createParkingViewModel.onPriceMinuteChange(it) },
                label = { Text("Price Minute") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Location: Lat = ${createParkingViewModel.latitude.value}, Lon = ${createParkingViewModel.longitude.value}")
            Button(onClick = { navController.navigate("SelectLocationScreen") }) {
                Text("Select Location")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                Text("Select Image")
            }

            createParkingViewModel.selectedImage.value?.let {
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp).height(300.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (createParkingViewModel.selectedImage.value != null) {
                        // Lanza una corrutina para llamar a onAddParking
                        CoroutineScope(Dispatchers.Main).launch {
                            createParkingViewModel.onAddParking(context, selectLocationViewModel)
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Please select an image -> CreateParkingScreen",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    navController.navigate("parkingList")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear Parking")
            }
        }
    }
}