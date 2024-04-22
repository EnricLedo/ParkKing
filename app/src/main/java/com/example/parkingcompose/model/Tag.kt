package com.example.parkingcompose.model

data class Tag (
    var id: String? = null,
    val title: String = "",
    val content: String = "",
    val image: String = "",  // URL from Firebase Storage
    var parkingIds: List<String> = emptyList()  // Lista para almacenar los IDs de estacionamientos.
)