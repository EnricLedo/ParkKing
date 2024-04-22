package com.example.parkingcompose.viewmodels

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingcompose.model.Location
import com.example.parkingcompose.model.Parking
import com.example.parkingcompose.util.StorageUtil
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import com.example.parkingcompose.dao.UserDao
import com.example.parkingcompose.model.Tag
import com.google.firebase.firestore.FieldValue

class CreateParkingViewModel(private val tagViewModel: TagViewModel) : ViewModel() {
    var name = mutableStateOf("")
    var description = mutableStateOf("")
    var priceMinute = mutableStateOf("")
    var tags = mutableStateOf(listOf<Tag>())
    var selectedTagIds = mutableStateOf(listOf<String>())
    var latitude = mutableStateOf(0.0)
    var longitude = mutableStateOf(0.0)
    var selectedImage = mutableStateOf<Uri?>(null)
    val db = FirebaseFirestore.getInstance()
    val userDao = UserDao

    private val _parkingAddedEvent = MutableSharedFlow<Unit>()
    val parkingAddedEvent: SharedFlow<Unit> = _parkingAddedEvent

    private val _parkingEnabledEvent = MutableSharedFlow<Unit>()
    val parkingEnabledEvent: SharedFlow<Unit> = _parkingEnabledEvent

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

    suspend fun onAddParking(context: Context, selectLocationViewModel: SelectLocationViewModel, userDAO: UserDao) {
        val imageUrl = StorageUtil.uploadImageToFirebaseStorage(selectedImage.value)
        userDAO.getCurrentUsername { username ->
            if (imageUrl != null) {
                val selectedLocation = selectLocationViewModel.selectedLocation.value
                if (selectedLocation != null) {
                    val price = if (priceMinute.value.isNotEmpty()) {
                        priceMinute.value.toFloat()
                    } else {
                        Toast.makeText(context, "Please enter a price -> OnAddParking Method", Toast.LENGTH_SHORT).show()
                        return@getCurrentUsername
                    }

                    val parking = Parking(
                        id = db.collection("parkings").document().id,
                        location = Location(selectedLocation.latitude, selectedLocation.longitude),
                        name = name.value,
                        description = description.value,
                        image = imageUrl,
                        parkingRating = 0.0,
                        reviewList = emptyList(),
                        tags = selectedTagIds.value,
                        priceMinute = price,
                        createdBy = username
                    )
                    addParking(parking, context)
                    Toast.makeText(context, "Parking created. It will be published once moderated", Toast.LENGTH_LONG).show()
                    selectLocationViewModel.resetSelectedLocation()
                    resetFields()
                } else {
                    Toast.makeText(context, "Please select a location -> OnAddParking Method", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Failed to upload image -> OnAddParking Method", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addParking(parking: Parking, localContext: Context) {
        // Añade un nuevo documento a la colección "parkings"
        val newParkingRef = db.collection("parkings").document(parking.id)
        newParkingRef.set(parking)
            .addOnSuccessListener {
                Log.d(TAG, "Parking added with ID: ${newParkingRef.id}")
                viewModelScope.launch {
                    _parkingAddedEvent.emit(Unit) // Emit an update event
                    _parkingEnabledEvent.emit(Unit) // Emit an update event
                }
                updateTagsWithParkingId(newParkingRef.id) // Consider updating tags before emitting the event if necessary

            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding parking", e)
                Toast.makeText (localContext, "Error adding parking -> addParking Method", Toast.LENGTH_SHORT).show()
            }
    }

    private fun resetFields() {
        name.value = ""
        description.value = ""
        priceMinute.value = ""
        latitude.value = 0.0
        longitude.value = 0.0
        selectedImage.value = null
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