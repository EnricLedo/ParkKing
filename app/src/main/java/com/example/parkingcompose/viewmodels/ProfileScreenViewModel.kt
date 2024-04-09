package com.example.parkingcompose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingcompose.util.GoogleAuthUiClient
import kotlinx.coroutines.launch

class ProfileScreenViewModel(private val googleAuthUiClient: GoogleAuthUiClient) : ViewModel() {
    val userData = googleAuthUiClient.getSignedInUser()

    fun signOut() {
        viewModelScope.launch {
            googleAuthUiClient.signOut()
        }
    }
}