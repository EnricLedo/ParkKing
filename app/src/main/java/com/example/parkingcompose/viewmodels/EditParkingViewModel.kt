import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingcompose.dao.ParkingDAO
import com.example.parkingcompose.model.Parking
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class EditParkingViewModel(private val parkingDAO: ParkingDAO, private val parkingId: String) : ViewModel() {
    var parking = mutableStateOf<Parking?>(null)
    var selectedImage = mutableStateOf<Uri?>(null)
    var selectedLocation = mutableStateOf<LatLng?>(null)

    init {
        viewModelScope.launch {
            parking.value = parkingDAO.getParkingById(parkingId)
        }
    }

    suspend fun updateParking(updatedParking: Parking) {
        parkingDAO.updateParking(updatedParking)
    }
}