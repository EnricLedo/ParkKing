package com.example.parkingcompose.viewmodels
import androidx.compose.runtime.remember
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.parkingcompose.MyAppRoute
import com.example.parkingcompose.NavigationActions
import com.example.parkingcompose.data.User
import com.example.parkingcompose.data.UserData
import com.example.parkingcompose.domain.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch


class SearchScreenViewModel(
    private val signedInUser: UserData?,
    private val signOut: suspend () -> Unit
) : ViewModel() {

    fun getSignedInUser() = signedInUser

    fun signOut() {
        viewModelScope.launch {
            signOut()
        }
    }
}









