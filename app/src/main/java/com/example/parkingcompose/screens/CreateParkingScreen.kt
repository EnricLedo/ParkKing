package com.example.parkingcompose.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.parkingcompose.viewmodels.CreateParkingViewModel
import com.example.parkingcompose.viewmodels.SelectLocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CreateParkingScreen(viewModel: CreateParkingViewModel, selectLocationViewModel: SelectLocationViewModel, navController: NavController) {
    val context = LocalContext.current
    var image by remember { mutableStateOf<Uri?>(null) }
    val selectedLocation by selectLocationViewModel.selectedLocation.collectAsState()


// Actualiza la latitud y longitud cuando la ubicación seleccionada cambie
    viewModel.latitude.value = selectedLocation?.latitude ?: 0.0
    viewModel.longitude.value = selectedLocation?.longitude ?: 0.0
    LaunchedEffect(key1 = true) {
        viewModel.parkingAddedEvent.collect {
            navController.navigate("parkingList") // Adjust the route name as per your NavGraph
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> image = uri }
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Nombre del parking
            OutlinedTextField(
                value = viewModel.name.value,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Name") }
            )
        }
        item {
            // Descripción del parking
            OutlinedTextField(
                value = viewModel.description.value,
                onValueChange = { viewModel.onDescriptionChange(it) },
                label = { Text("Description") },
            )
        }
        item {
            // Precio por minuto
            OutlinedTextField(
                value = viewModel.priceMinute.value,
                onValueChange = { viewModel.onPriceMinuteChange(it) },
                label = { Text("Price Minute") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Location: Lat = ${viewModel.latitude.value}, Lon = ${viewModel.longitude.value}")
            Button(onClick = { navController.navigate("SelectLocationScreen") }) {
                Text("Select Location")
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }) {
                Text("Select Image")
            }
        }
        item {
            // Muestra la imagen seleccionada
            if (image != null) {
                AsyncImage(
                    model = image,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp).height(300.dp)
                )
            }
        }
        item {
            // Sección para añadir tags
            AddTagSection(viewModel)
        }
        item {
            // Botón para añadir parking
            Button(onClick = {
                if (image != null && selectedLocation != null) {
                    // Lanza una corrutina para llamar a onAddParking
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.onAddParking(context, image, selectLocationViewModel)
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Please select an image and location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
                Text("Add Parking")
            }
        }
    }
}

@Composable
fun AddTagSection(viewModel: CreateParkingViewModel) {
    val tags = viewModel.tags.value  // Asegúrate de que Tag tiene un campo 'id'

    LazyRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tags) { tag ->
            val isSelected = tag.id in viewModel.selectedTagIds.value
            Text(
                text = tag.title,
                modifier = Modifier
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    )
                    .clickable {
                        tag.id?.let { viewModel.selectTag(it, !isSelected) }
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
