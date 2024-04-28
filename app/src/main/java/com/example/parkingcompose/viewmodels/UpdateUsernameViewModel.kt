package com.example.parkingcompose.viewmodels

import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.parkingcompose.dao.UserDao
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class UpdateUsernameViewModel() : ViewModel() {
    val auth = Firebase.auth
    var username = mutableStateOf(TextFieldValue("")) // Cambia a variable mutable
    var currentUsername = mutableStateOf("")
    val userDao = UserDao()
    init {
        getCurrentUsername()
    }

    private val _isUsernameUpdated = MutableLiveData<Boolean>()
    val isUsernameUpdated: LiveData<Boolean> get() = _isUsernameUpdated

    fun tryUpdateUsername() {
        viewModelScope.launch {
            _isUsernameUpdated.value = updateUsername()
        }
    }

    fun getCurrentUsername() {
        val email = auth.currentUser?.email
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    currentUsername.value = document.getString("username") ?: ""
                }
            }
    }

    suspend fun updateUsername(): Boolean {
        var isUpdated = false
        if (username.value.text.isNotEmpty()) {
            val isAvailable = userDao.checkUsernameAvailable(username.value.text)

            if (isAvailable) {
                val userId = auth.currentUser?.uid

                isUpdated = userDao.updateUsernameInDb(userId, username.value.text)
                if (isUpdated) {
                    username.value = TextFieldValue("")
                    getCurrentUsername()
                }
            }
        }
        return isUpdated
    }


}
