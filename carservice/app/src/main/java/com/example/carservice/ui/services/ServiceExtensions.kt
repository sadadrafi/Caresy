package com.example.carservice.ui.services

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toService(): Service {
    Log.d("Firestore", "Document data: ${this.data}") // Menambahkan log untuk melihat isi data
    return Service(
        serviceId = this.getString("service_id") ?: "",
        name = this.getString("name") ?: "Unknown",
        description = this.getString("description") ?: "No description",
        price = this.get("price")?.toString() ?: "0.0",
        icon = this.getString("icon") ?: "default_icon",
        duration = this.getString("duration") ?: "",
        username = this.getString("username") ?: "",
        bookingId = this.getString("booking_id") ?: "",
        selectedDate = this.getString("selected_date") ?: "",
        selectedTime = this.getString("selected_time") ?: "",
        vehicle = this.getString("vehicle") ?: "",
        paymentMethod = this.getString("selected_payment_method") ?: ""
    )
}
