package com.example.parkingcompose.viewmodels

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingcompose.data.Location
import com.example.parkingcompose.data.Parking
import com.example.parkingcompose.data.Tag
import com.example.parkingcompose.util.StorageUtil
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch




class CreateParkingViewModel(private val tagViewModel: TagViewModel) : ViewModel() {
    var name = mutableStateOf("")
    var description = mutableStateOf("")
    var priceMinute = mutableStateOf("")
    var tags = mutableStateOf(listOf<Tag>())
    var selectedTagIds = mutableStateOf(listOf<String>())
    var latitude = mutableStateOf(0.0)
    var longitude = mutableStateOf(0.0)
    private val _parkingAddedEvent = MutableSharedFlow<Unit>()
    val parkingAddedEvent: SharedFlow<Unit> = _parkingAddedEvent


    init {
        observeTags()
    }

    private fun observeTags() {
        viewModelScope.launch {
            tagViewModel.getTagsFlow().collect { tagsList ->
                tags.value = tagsList
            }
        }
    }


    fun onNameChange(newValue: String) {
        name.value = newValue
    }

    fun onDescriptionChange(newValue: String) {
        description.value = newValue
    }

    fun onPriceMinuteChange(newValue: String) {
        priceMinute.value = newValue
    }
    fun selectTag(tagId: String, isSelected: Boolean) {
        selectedTagIds.value = if (isSelected) {
            selectedTagIds.value + tagId
        } else {
            selectedTagIds.value - tagId
        }
    }
    suspend fun onAddParking(context: Context, image: Uri?,selectLocationViewModel: SelectLocationViewModel) {
        val imageUrl = StorageUtil.uploadImageToFirebaseStorage(image)
        if (imageUrl != null) {
            // Obtiene la ubicación seleccionada
            val selectedLocation = selectLocationViewModel.selectedLocation.value

            if (selectedLocation != null) {
                val price = if (priceMinute.value.isNotEmpty()) {
                    priceMinute.value.toFloat()
                } else {
                    // Set a default value or show an error message
                    Toast.makeText(context, "Please enter a price -> OnAddParking Method", Toast.LENGTH_SHORT).show()
                    return
                }
                val parking = Parking(
                    id = "",
                    location = Location(selectedLocation.latitude, selectedLocation.longitude),
                    name = name.value,
                    description = description.value,
                    image = imageUrl,
                    parkingRating = 0.0f,
                    reviewList = emptyList(),
                    tags = selectedTagIds.value,
                    priceMinute = priceMinute.value.toFloat()
                )
                addParking(parking, context)
            } else {
                Toast.makeText(context, "Please select a location -> OnAddParking Method", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Failed to upload image -> OnAddParking Method", Toast.LENGTH_SHORT).show()
        }
    }


    private fun addParking(parking: Parking, localContext: Context) {
        val db = FirebaseFirestore.getInstance()
        db.collection("parkings").add(parking)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Parking added with ID: ${documentReference.id}")
                viewModelScope.launch {
                    _parkingAddedEvent.emit(Unit) // Emit the event after successful addition
                }
                updateTagsWithParkingId(documentReference.id) // Consider updating tags before emitting the event if necessary
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding parking", e)
                Toast.makeText(localContext, "Error adding parking", Toast.LENGTH_SHORT).show()
            }
    }


    private fun updateTagsWithParkingId(parkingId: String) {
        val db = FirebaseFirestore.getInstance()
        selectedTagIds.value.forEach { tagId ->
            db.collection("tags").document(tagId)
                .update("parkingIds", FieldValue.arrayUnion(parkingId))
                .addOnSuccessListener {
                    Log.d(TAG, "Parking ID added to tag successfully: $parkingId")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding parking ID to tag", e)
                }
        }
    }

}

