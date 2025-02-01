package com.example.carservice.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {

    private val db = FirebaseFirestore.getInstance()

    // Fungsi untuk menyimpan data pengguna ke Firestore
    fun saveUserData(userId: String, user: User) {
        val userRef = db.collection("users").document(userId)
        userRef.set(user)
            .addOnSuccessListener {
                Log.d("UserRepository", "User data saved successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Error saving user data", e)
            }
    }

    // Fungsi untuk mengambil data pengguna dari Firestore
    fun getUserData(userId: String, callback: (User?) -> Unit) {
        val userRef = db.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Error fetching user data", e)
                callback(null)
            }
    }
}
