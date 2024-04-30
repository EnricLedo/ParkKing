import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingcompose.dao.ParkingDAO
import com.example.parkingcompose.model.Parking
import android.net.Uri
import android.widget.Toast
import com.example.parkingcompose.model.Tag
import com.example.parkingcompose.viewmodels.TagViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class EditParkingViewModel(private val parkingDAO: ParkingDAO, private val parkingId: String, private val tagViewModel: TagViewModel) : ViewModel() {
    var parking = mutableStateOf<Parking?>(null)
    var selectedImage = mutableStateOf<Uri?>(null)
    var tags = mutableStateOf(listOf<Tag>())
    var selectedTagIds = mutableStateOf(listOf<String>())

    val name = mutableStateOf("")
    val description = mutableStateOf("")
    val priceMinute = mutableStateOf("")
    val image = mutableStateOf<Uri?>(null)


    fun selectTag(tagId: String, isSelected: Boolean) {
        selectedTagIds.value = if (isSelected) {
            selectedTagIds.value + tagId
        } else {
            selectedTagIds.value - tagId
        }
    }

    private fun observeTags() {
        viewModelScope.launch {
            tagViewModel.getTagsFlow().collect { tagsList ->
                tags.value = tagsList
            }
        }
    }

    init {
        viewModelScope.launch {
            parking.value = parkingDAO.getParkingById(parkingId)
        }
        observeTags()
    }

    suspend fun updateParking(updatedParking: Parking, context: Context) {
        parkingDAO.updateParking(updatedParking)

        Toast.makeText(context, "Parking modified.  It will be published once moderated", Toast.LENGTH_SHORT).show()
    }


}