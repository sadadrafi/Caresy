package com.example.carservice.ui.services

// Data class
data class Service(
    val username: String,
    val bookingId: String,
    val selectedDate : String,
    val selectedTime : String,
    val vehicle : String,
    val paymentMethod : String,
    val serviceId: String,
    val name: String,
    val description: String,
    val price: String,
    val icon: String,
    val duration: String
)
