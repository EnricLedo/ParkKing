package com.example.parkingcompose.screens

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
import com.example.parkingcompose.viewmodels.CreateParkingViewModel
import com.example.parkingcompose.viewmodels.LanguageViewModel
import com.example.parkingcompose.viewmodels.SelectLocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CreateParkingScreen(
    createParkingViewModel: CreateParkingViewModel,
    selectLocationViewModel: SelectLocationViewModel,
    navController: NavHostController, viewModel: LanguageViewModel,
    userDao: UserDao
) {
    var image by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val selectedLocation by selectLocationViewModel.selectedLocation.collectAsState()

    // Recursos de strings localizados
    val strCreateParking = stringResource(id = R.string.create_parking)
    val strParkingName = stringResource(id = R.string.parking_name)
    val strParkingDescription = stringResource(id = R.string.parking_description)
    val strPriceMinute = stringResource(id = R.string.price_minute)
    val strSelectLocation = stringResource(id = R.string.select_location)
    val strSelectImage = stringResource(id = R.string.select_image)
    val strCreate = stringResource(id = R.string.create)

    LaunchedEffect(key1 = true) {
        createParkingViewModel.parkingAddedEvent.collect {
            navController.navigate("parkingList") // Adjust the route name as per your NavGraph
        }
    }

    createParkingViewModel.latitude.value = selectedLocation?.latitude ?: 0.0
    createParkingViewModel.longitude.value = selectedLocation?.longitude ?: 0.0

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> createParkingViewModel.selectedImage.value = uri }
    )

    Column {
        LanguageSelector(viewModel)
    }
    LazyColumn(

        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Text(
                text = strCreateParking,
                fontSize = 28.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = createParkingViewModel.name.value,
                onValueChange = { createParkingViewModel.onNameChange(it) },
                label = { Text(strParkingName) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = createParkingViewModel.description.value,
                onValueChange = { createParkingViewModel.onDescriptionChange(it) },
                label = { Text(strParkingDescription) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = createParkingViewModel.priceMinute.value,
                onValueChange = { createParkingViewModel.onPriceMinuteChange(it) },
                label = { Text(strPriceMinute) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.navigate("selectLocation") }) {
                Text(strSelectLocation)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                Text(strSelectImage)
            }

            createParkingViewModel.selectedImage.value?.let {
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp).height(300.dp)
                )
            }
            AddTagSection(createParkingViewModel)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (createParkingViewModel.selectedImage.value != null) {
                        CoroutineScope(Dispatchers.Main).launch {
                            createParkingViewModel.onAddParking(context, selectLocationViewModel,userDao)
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Please select an image or Location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    navController.navigate("parkingList")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(strCreate)
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
