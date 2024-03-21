package com.example.parkingcompose.data

data class User(
    var username: String,
    val email: String,
    val rol: Rol,
    val id: String
){
    fun toMap():MutableMap<String,Any>{
        return mutableMapOf(
            "user_id" to this.id,
            "username" to this.username,
            "rol" to this.rol,
            "email" to this.email
        )
    }
}

enum class Rol {
    Admin, User
}


