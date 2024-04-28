package com.example.parkingcompose.screens

import EditParkingViewModel
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.parkingcompose.R
import com.example.parkingcompose.dao.UserDao
import com.example.parkingcompose.viewmodels.SelectLocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.parkingcompose.dao.ParkingDAO
import com.example.parkingcompose.model.EditParkingViewModelFactory
import com.example.parkingcompose.viewmodels.CreateParkingViewModel
import com.example.parkingcompose.viewmodels.TagViewModel

@Composable
fun EditParkingScreen(
    navController: NavHostController,
    parkingDAO: ParkingDAO,
    parkingId: String,
    selectLocationViewModel: SelectLocationViewModel,
    tagViewModel: TagViewModel,
    userDao: UserDao
) {
    val editParkingViewModelFactory = EditParkingViewModelFactory(parkingDAO, parkingId, tagViewModel)
    val editParkingViewModel: EditParkingViewModel = viewModel(factory = editParkingViewModelFactory)
    val parking = editParkingViewModel.parking.value

    val context = LocalContext.current
    val selectedLocation by selectLocationViewModel.selectedLocation.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> editParkingViewModel.selectedImage.value = uri }
    )

    if (parking != null) {
        // Mover la inicialización de los estados mutables aquí
        var name by remember { mutableStateOf(parking.name ?: "") }
        var description by remember { mutableStateOf(parking.description ?: "") }
        var priceMinute by remember { mutableStateOf(parking.priceMinute.toString() ?: "") }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Edit Parking",
                    fontSize = 28.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Parking Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Parking Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = priceMinute,
                    onValueChange = { priceMinute = it },
                    label = { Text("Price per Minute") },
                    modifier = Modifier.fillMaxWidth()
                )


                editParkingViewModel.selectedImage.value?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .height(300.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                AddTagSection(parking.tags, editParkingViewModel)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                                // Actualizar el objeto Parking con los nuevos valores antes de llamar a updateParking
                                parking.name = name
                                parking.description = description
                                parking.priceMinute = priceMinute.toFloat()
                                parking.tags = editParkingViewModel.selectedTagIds.value
                                editParkingViewModel.updateParking(parking)
                            }

                        navController.navigate("parkingList")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update")
                }
            }
        }
    } else {
        Text("Loading...")
    }
}
@Composable
fun AddTagSection(previousTags: List<String>,viewModel: EditParkingViewModel) {
    //Le pasamos el previousTags para que las etiquetas aparezcan seleccionadas
    viewModel.selectedTagIds = remember { mutableStateOf(previousTags) }

    val tags = viewModel.tags.value

    LazyRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tags) { tag ->
            val isSelected = tag.title in viewModel.selectedTagIds.value
            Text(
                text = tag.title,
                modifier = Modifier
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    )
                    .clickable {
                        viewModel.selectTag(tag.title, !isSelected)
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
