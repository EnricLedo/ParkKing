package com.example.parkingcompose.viewmodels

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.parkingcompose.data.Parking
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class CrearParkingViewModel : ViewModel() {
    var name = mutableStateOf("")
    var description = mutableStateOf("")
    var priceMinute = mutableStateOf("")
    var image = mutableStateOf<ImageBitmap?>(null)

    fun onNameChange(newValue: String) {
        name.value = newValue
    }

    fun onDescriptionChange(newValue: String) {
        description.value = newValue
    }

    fun onPriceMinuteChange(newValue: String) {
        priceMinute.value = newValue
    }

    fun onSelectImage(context: Context) {
        // Aquí debes manejar la selección de la imagen
    }

    var imagePickerLauncher: ((String) -> Unit)? = null



    fun onAddParking(context: Context) {
        val defaultBitmap = if (image.value == null) {
            val drawableId = context.resources.getIdentifier("img_parkking_default", "drawable", context.packageName)
            BitmapFactory.decodeResource(context.resources, drawableId).asImageBitmap()
        } else {
            image.value!!
        }

        val parking = Parking(
            parkingId = 0, // Esto deberías manejarlo según tu lógica
            location = Pair(0.0, 0.0), // Aquí debes manejar la ubicación según la lógica de tu aplicación
            name = name.value,
            description = description.value,
            image = bitmapToBase64(defaultBitmap.asAndroidBitmap()),
            parkingRating = 0.0f, // Podrías establecer un valor predeterminado
            reviewList = emptyList(), // Inicializar como una lista vacía
            tagList = emptyList(), // Inicializar como una lista vacía
            priceMinute = priceMinute.value.toFloatOrNull() ?: 0.0f // Convertir a Float o establecer un valor predeterminado si no se puede convertir
        )
        addParking(parking, context)
    }

    private fun addParking(parking: Parking, localContext: Context) {
        val db = FirebaseFirestore.getInstance()

        // Añade un nuevo documento a la colección "parkkings"
        db.collection("parkkings")
            .add(parking)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Parking added with ID: ${documentReference.id}")

            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding parking", e)
            }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}