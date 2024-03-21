package com.example.parkingcompose

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.parkingcompose.data.Parkking
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.parkingcompose.ui.theme.DaleComposeTheme
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class CreateParkking : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DaleComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    createParkking()
                }
            }
        }
    }
}

@Preview
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun createParkking() {
    val name = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val priceMinute = remember { mutableStateOf("") }
    val image = remember { mutableStateOf<ImageBitmap?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val source = ImageDecoder.createSource(context.contentResolver, it)
            val bitmap = ImageDecoder.decodeBitmap(source)
            image.value = bitmap.asImageBitmap()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
        )
    {
        OutlinedTextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text("Name") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { /* Handle next key event */ })
        )

        OutlinedTextField(
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text("Description") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { /* Handle next key event */ })
        )

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Select Image")
        }

        image.value?.let {
            Image(bitmap = it, contentDescription = "Selected Image")
        }

        OutlinedTextField(
            value = priceMinute.value,
            onValueChange = { priceMinute.value = it },
            label = { Text("Price Minute") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { /* Handle done key event */ })
        )
        Button(onClick = {
            val defaultBitmap = if (image.value == null) {
                val drawableId = context.resources.getIdentifier("img_parkking_default", "drawable", context.packageName)
                BitmapFactory.decodeResource(context.resources, drawableId).asImageBitmap()
            } else {
                image.value!!
            }

            val parking = Parkking(
                parking_id = 0, // Esto deberías manejarlo según tu lógica
                location = Pair(0.0, 0.0), // Aquí debes manejar la ubicación según la lógica de tu aplicación
                name = name.value,
                description = description.value,
                image = bitmapToBase64(defaultBitmap.asAndroidBitmap()),
                parking_rating = 0.0f, // Podrías establecer un valor predeterminado
                review_list = emptyList(), // Inicializar como una lista vacía
                tag_list = emptyList(), // Inicializar como una lista vacía
                price_minute = priceMinute.value.toFloatOrNull() ?: 0.0f // Convertir a Float o establecer un valor predeterminado si no se puede convertir
            )
            addParking(parking,context)

        }) {
            Text("Add Parking")
        }

    }
}

private fun addParking(parking: Parkking, localContext: Context) {
    val db = FirebaseFirestore.getInstance()

    // Añade un nuevo documento a la colección "parkings"
    db.collection("parkkings")
        .add(parking)
        .addOnSuccessListener { documentReference ->
            Log.d(TAG, "Parking added with ID: ${documentReference.id}")
            val intent = Intent(localContext, ParkingListActivity::class.java)
            ContextCompat.startActivity(localContext, intent, null)
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Error adding parking", e)
        }
}

fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}