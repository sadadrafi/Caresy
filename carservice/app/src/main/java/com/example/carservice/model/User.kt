package com.example.carservice.model

data class User(
    val username: String = "",
    val email: String = "",
    val role: String = "user" // Role default jika belum ada
)
