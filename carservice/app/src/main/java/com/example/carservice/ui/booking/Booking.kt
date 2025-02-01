package com.example.carservice.ui.booking

data class Booking(
    var username: String = "",
    var bookingId: String = "",
    var serviceId: String = "",
    var serviceName: String = "",
    var serviceDate: String = "",
    var serviceTime: String = "",
    var servicePrice: String = "",
    var serviceDuration: String = "",
    var serviceDescription: String = "",
    var paymentMethod: String = "",
    var vehicle: String = "",
    var status: String = ""
)
